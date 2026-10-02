import json
import time
import uuid
import redis
from sqlmodel import Session, create_engine, select
from .config import settings
from .models import Order
from .services.watermarker import stamp_pdf_watermark

engine = create_engine(settings.DATABASE_URL)
r = redis.Redis(
    host=settings.REDIS_HOST,
    port=settings.REDIS_PORT,
    password=settings.REDIS_PASSWORD or None,
    ssl=settings.REDIS_SSL,
    socket_timeout=15,
    socket_connect_timeout=10,
    decode_responses=True
)

QUEUE_NAME = "order_tasks"

def process_watermark_task(task_data: dict):
    order_id_str = task_data.get("order_id")
    buyer_email = task_data.get("buyer_email")
    buyer_phone = task_data.get("buyer_phone")
    master_file_url = task_data.get("master_file_url")

    print(f"[worker] Starting watermark task for order: {order_id_str}")

    try:
        watermarked_path = stamp_pdf_watermark(
            input_pdf_path=master_file_url,
            order_id=order_id_str,
            buyer_email=buyer_email,
            buyer_phone=buyer_phone
        )

        with Session(engine) as session:
            order_uuid = uuid.UUID(order_id_str)
            order = session.exec(select(Order).where(Order.id == order_uuid)).first()

            if order:
                order.watermarked_file_url = watermarked_path
                session.add(order)
                session.commit()
                print(f"[worker] Successfully updated Order {order_id_str} with watermarked PDF: {watermarked_path}")
            else:
                print(f"[worker] Order {order_id_str} not found in database")
    except Exception as e:
        print(f"[worker] Error processing watermark for order {order_id_str}: {str(e)}")

def main():
    print(f"[*] Anti-Piracy PDF DRM Worker listening on Redis queue: '{QUEUE_NAME}'...")
    while True:
        try:
            item = r.blpop(QUEUE_NAME, timeout=5)
            if item:
                _, payload_str = item
                task_data = json.loads(payload_str)

                if task_data.get("event") == "WATERMARK_PDF":
                    process_watermark_task(task_data)
        except (redis.ConnectionError, redis.TimeoutError):
            # print("[worker] Redis connection error, retrying in 5 seconds...")
            time.sleep(5)
        except Exception as e:
            print(f"[worker] Unexpected loop error: {e}")
            time.sleep(1)

if __name__ == "__main__":
    main()
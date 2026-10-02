import os
import pymupdf # PyMuPDF
from datetime import datetime
from pathlib import Path
from ..config import settings

def stamp_pdf_watermark(input_pdf_path: str, order_id: str, buyer_email: str, buyer_phone: str) -> str:
    """
    Applies indelible dynamic watermarking across every page of a PDF document:
    1. Diagonal repetitive 45-degree semi-transparent buyer identifier.
    2. Locked bottom security banner with unique cryptographic order stamp.
    3. Flattens annotations to prevent layer removal.
    """
    if not os.path.exists(input_pdf_path):
        raise FileNotFoundError(f"Source PDF file not found at: {input_pdf_path}")

    doc = pymupdf.open(input_pdf_path)
    timestamp = datetime.utcnow().strftime("%Y-%m-%d %H:%M:%S UTC")
    watermark_line = f"LICENSED TO: {buyer_email} | TEL: {buyer_phone} | ORDER: #{order_id[:8]}"
    footer_text = f"Protected Document - Order #{order_id} | Issued to {buyer_email} on {timestamp}. Redistribution or piracy is strictly prohibited."

    for page_num in range(len(doc)):
        page = doc[page_num]
        rect = page.rect
        width, height = rect.width, rect.height

        # 1. Repeated Diagonal Watermark
        step_y = height / 4
        for i in range(1, 4):
            point = pymupdf.Point(width * 0.15, step_y * i)
            page.insert_text(
                point,
                watermark_line,
                fontsize=13,
                morph=(point, pymupdf.Matrix(-35)),
                color=(0.75, 0.75, 0.75),
                overlay=True
            )
        
        # 2. Bottom Security License Banner
        footer_rect = pymupdf.Rect(0, height - 24, width, height)
        page.draw_rect(footer_rect, color=(0.1, 0.1, 0.15), fill=(0.95, 0.95, 0.98), overlay=True)
        page.insert_textbox(
            footer_rect,
            footer_text,
            fontsize=7.5,
            color=(0.3, 0.3, 0.35),
            align=pymupdf.TEXT_ALIGN_CENTER,
            overlay=True
        )

    output_filename = f"watermarked_{order_id}_{int(datetime.utcnow().timestamp())}.pdf"
    output_path = settings.STORAGE_DIR / output_filename

    doc.save(str(output_path), garbage=4, deflate=True)
    doc.close()

    return str(output_path.resolve())
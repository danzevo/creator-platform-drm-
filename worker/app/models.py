import uuid
from datetime import datetime
from typing import Optional
from sqlmodel import SQLModel, Field

class Order(SQLModel, table=True):
    __tablename__ = "orders"

    id: uuid.UUID = Field(default_factory=uuid.uuid4, primary_key=True)
    product_id: uuid.UUID
    creator_id: uuid.UUID
    buyer_email: str
    buyer_phone: str
    total_amount: float
    status: str
    payment_ref: Optional[str] = None
    payment_method: Optional[str] = None
    watermarked_file_url: Optional[str] = None
    created_at: Optional[datetime] = None
    updated_at: Optional[datetime] = None
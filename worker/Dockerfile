FROM python:3.11-slim

WORKDIR /app

# Install system dependencies required for PyMuPDF & psycopg2
RUN apt-get update && apt-get install -y --no-install-recommends \
    gcc \
    libpq-dev \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy worker application
COPY app ./app

# Create mount point for shared backend storage
RUN mkdir -p /app/backend/storage/watermarked

CMD ["python", "-m", "app.main"]
# Stage 1: Build Vue 3 with Vite
FROM node:20-alpine AS builder
WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

# Stage 2: Production Nginx Server
FROM nginx:alpine

# Copy custom Nginx configuration
COPY <<EOF /etc/nginx/conf.d/default.conf
server {
    listen 80;
    server_name localhost;

    # Serve Vue 3 SPA static files
    location / {
        root /usr/share/nginx/html;
        index index.html index.html;
        try_files \$uri \$uri/ /index.html;
    }

    # Reverse proxy API requests to Spring Boot container
    location /api/ {
        proxy_pass http://backend:8080/api/;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOF

COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
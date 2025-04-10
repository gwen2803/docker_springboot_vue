# frontend/Dockerfile

FROM node:20-alpine
WORKDIR /app
COPY . .
RUN npm install -g vite && \
    npm install && \
    vite build

EXPOSE 5173
CMD ["vite"]


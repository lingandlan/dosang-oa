# Simple frontend Dockerfile
FROM nginx:alpine

# Default static page
RUN echo '<!DOCTYPE html><html><head><title>OA System</title></head><body><h1>OA System Frontend</h1><p>Frontend will be deployed here.</p></body></html>' > /usr/share/nginx/html/index.html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]

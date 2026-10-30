FROM nginx:1.27-alpine
# Config is baked into the image (no bind mount) so the stack also deploys
# from a Jenkins container talking to the host Docker daemon
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80

### STAGE 1: Build ###
FROM node:14.0.0 AS build

WORKDIR /usr/src/app

RUN npm install -g @angular/cli

RUN npm cache clean -f

COPY package.json package.json
RUN npm install
COPY . .
RUN ng build --prod

### STAGE 2: Run ###
FROM nginx:1.17.6-alpine
COPY --from=build /usr/src/app/dist/frontend /usr/share/nginx/html

ENTRYPOINT ["nginx", "-g","daemon off;"]

EXPOSE 80
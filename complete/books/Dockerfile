FROM oracle/graalvm-ce:20.0.0-java8 as graalvm
RUN gu install native-image

COPY . /home/app/books
WORKDIR /home/app/books

RUN native-image --no-server -cp build/libs/books-*-all.jar

FROM frolvlad/alpine-glibc
RUN apk update && apk add libstdc++
EXPOSE 8080
COPY --from=graalvm /home/app/books /app/books
ENTRYPOINT ["/app/books", "-Xmx68m"]

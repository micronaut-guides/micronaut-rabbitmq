FROM oracle/graalvm-ce:19.3.0-java8 as graalvm
COPY . /home/app/books
WORKDIR /home/app/books
RUN gu install native-image
RUN native-image --no-server -cp build/libs/books-*-all.jar

FROM frolvlad/alpine-glibc
EXPOSE 8080
COPY --from=graalvm /home/app/books /app/books
ENTRYPOINT ["/app/books","-Xmx68m"]
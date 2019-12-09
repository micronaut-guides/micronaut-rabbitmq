FROM oracle/graalvm-ce:19.3.0-java8 as graalvm
COPY . /home/app/analytics
WORKDIR /home/app/analytics
RUN gu install native-image
RUN native-image --no-server -cp build/libs/analytics-*-all.jar

FROM frolvlad/alpine-glibc
EXPOSE 8080
COPY --from=graalvm /home/app/analytics /app/analytics
ENTRYPOINT ["/app/analytics","-Xmx68m"]
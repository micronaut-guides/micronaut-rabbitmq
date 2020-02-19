FROM oracle/graalvm-ce:20.0.0-java8 as graalvm
RUN gu install native-image

COPY . /home/app/analytics
WORKDIR /home/app/analytics

RUN native-image --no-server -cp build/libs/analytics-*-all.jar

FROM frolvlad/alpine-glibc
RUN apk update && apk add libstdc++
EXPOSE 8080
COPY --from=graalvm /home/app/analytics /app/analytics
ENTRYPOINT ["/app/analytics", "-Xmx68m"]

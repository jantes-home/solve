# ShortLink 

## Summary

ShortLink is a URL shortening service where you enter a URL such as https://example.com/library/react and it returns a short URL such as http://short.est/GeAi9K.

## Endpoints

### /encode

Returns the shortlink URL for the specified URL.

Parameters:
- url  The original URL

Responses:
- 200 : Successful operation
```json
{
  "originalUrl": "https://example.com/library/react",
  "shortUrl": "http://short.est/PdE6o"
}
```
- 400 : malformed URL supplied

### /decode

Retrieves the original URL encoded by the given shortlink URL.

Parameters:
- url  The shortlink URL

Responses:
- 200 : Successful operation
```json
{
  "originalUrl": "https://example.com/library/react",
  "shortUrl": "http://short.est/PdE6o"
}
```
- 400 : malformed URL supplied
- 404 : unknown URL supplied. No URL has been encoded to produce this shortlink

## Build

Build using maven, then build a run the docker file.

```
cd short
mvn install
docker build -t shortlink .
docker run -p 8080:8080 shortlink
```

You can then test with curl

```
curl http://localhost:8080/encode?url=https://example.com/library/react
```

and

```
curl http://localhost:8080/decode?url=http://short.est/PdE6o
```

Both of these call should produce:

```json
{"originalUrl":"https://example.com/library/react","shortUrl":"http://short.est/PdE6o"}
```


## Links

- [Swagger Docs](https://github.com/jantes-home/solve/blob/main/short/src/main/resources/swagger.yaml)
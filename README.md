# ip-address-util
Utils for IP addresses

## Build the library

Build with Maven:
```bash
mvn clean package
```

## Dump IP addresses from IP network masks

Run the program to process a semicolon-separated input file:
```bash
java -jar target/ip-address-util-1.0.0-SNAPSHOT.jar masks-semicolon-separated.csv \;
```

Run the program to process a comma-separated input file:
```bash
java -jar target/ip-address-util-1.0.0-SNAPSHOT.jar masks-comma-separated.csv \,
```

The output will be written to a file with `_addresses` suffix. The first row with column headers will be retained.

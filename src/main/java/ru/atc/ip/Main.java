package ru.atc.ip;

import java.io.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Main {
    private static final char defaultSeparator = ';';

    private static void printHelp() {
        System.out.println("Usage:");
        System.out.println(" <command> input_csv_file [separator]");
        System.out.println(" <command> -h");
        System.out.println(" where");
        System.out.println(" input_csv_file - Path to the input csv file. Mandatory argument.");
        System.out.println("   File format: <IP mask or address><separator><other fields...>");
        System.out.println(" separator - Separator used both in input and output files. Optional argument.");
        System.out.println("   Default: '" + defaultSeparator + "'");
        System.out.println(" The output file with IP addresses will be created in the same directory as the input file." +
                " It will have the same separator as the input file.");
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Incorrect number of arguments");
            printHelp();
            System.exit(1);
        }
        if ("-h".equals(args[0]) || "-help".equals(args[0]) || "--help".equals(args[0])) {
            printHelp();
            System.exit(0);
        }

        String masksFile = args[0];
        char separator = args.length > 1 ? args[1].charAt(0) : defaultSeparator;
        System.out.println("Reading IP masks from file: " + masksFile + " with separator: " + separator);

        String ipAddressesHeader = getAddressesHeader(masksFile, separator);
        Stream<String> ipMasks = readIpMasks(masksFile);
        Stream<String> ipAddresses = ipMasks
                .flatMap(csvStr -> maskToAddresses(csvStr, separator));

        String addressesFile = getAddressesFile(masksFile);
        System.out.println("Writing IP addresses to file: " + addressesFile);
        writeIpAddresses(ipAddresses, ipAddressesHeader, addressesFile);
    }

    private static Stream<String> readIpMasks(String masksFile) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(masksFile));
        Pattern startsWithNumber = Pattern.compile("^\\d+.+");
        return reader.lines()
                .filter(str -> startsWithNumber.matcher(str).matches());
    }

    private static Stream<String> maskToAddresses(String csvStr, char separator) {
        int indexOfSeparator = csvStr.indexOf(separator);
        if (indexOfSeparator < 0) {
            System.err.println("Unexpected csv string; skipping: " + csvStr);
            return Stream.empty();
        }
        String mask = csvStr.substring(0, indexOfSeparator);
        String restStr = csvStr.substring(indexOfSeparator);
        return IpMask.getAllIpAddresses(mask)
                .map(address -> address + restStr);
    }

    private static String getAddressesHeader(String masksFile, char separator) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(masksFile));
        String masksHeader = reader.readLine();
        int indexOfSeparator = masksHeader.indexOf(separator);
        if (indexOfSeparator < 0) {
            System.err.println("Malformed header in the masks file - separator not found: " + masksHeader);
            System.err.println("Exiting");
            System.exit(1);
        }

        return "IP address" + masksHeader.substring(indexOfSeparator);
    }

    private static String getAddressesFile(String masksFile) {
        int indexOfDot = masksFile.lastIndexOf('.');
        if (indexOfDot < 0) {
            return masksFile + "_addresses.csv";
        }
        return masksFile.substring(0, indexOfDot) + "_addresses.csv";
    }

    private static void writeIpAddresses(Stream<String> ipAddresses, String header, String fileName) throws IOException {
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)), true);
        writer.println(header);
        ipAddresses.forEach(writer::println);
        writer.close();
    }

}

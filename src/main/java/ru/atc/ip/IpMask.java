package ru.atc.ip;

import org.apache.commons.net.util.SubnetUtils;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class IpMask {
    public static final String IP_ADDRESS = "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})";
    private static final Pattern addressPattern = Pattern.compile(IP_ADDRESS);

    public static Stream<String> getAllIpAddresses(String ipMask) {
        if (addressPattern.matcher(ipMask).matches()) {
            return Stream.of(ipMask);
        }

        try {
            SubnetUtils subnetUtils = new SubnetUtils(ipMask);
            return Arrays.stream(subnetUtils.getInfo().getAllAddresses());
        } catch (IllegalArgumentException e) {
            System.err.println("IP mask is illegal; skipping: " + ipMask);
            return Stream.empty();
        }
    }
}

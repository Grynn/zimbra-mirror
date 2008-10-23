package com.zimbra.cs.offline.gab;

public class AddressTest {
    private static final String ADDRESSES[] = {
        "1747A Stockton St., San Francisco, CA 94133, USA",
        "235 Montgomery St.\r\n\r\n15th Floor\r\nSan Francisco\r\nCalifornia 94133-4544\r\nUnited States\r\n",
        "235 Montgomery St., 15th Floor, San Francisco",
        "San Francisco, CA",
        "1234 Main Street, Scarsdale, NY"
    };

    private static final String NAMES[] = {
        "David Connelly",
        "Connelly",
        "Mr. Connelly",
        "David W. Connelly",
        "D. Connelly",
        "Mr. D. W. Connelly"
    };

    public static void main(String... args) {
        for (String addr : ADDRESSES) {
            System.out.println(Address.parse(addr).toString());
        }
        for (String name : NAMES) {
            System.out.println(Name.parse(name).toString());
        }
    }
}

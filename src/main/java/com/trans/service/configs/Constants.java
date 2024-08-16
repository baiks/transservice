package com.trans.service.configs;

public class Constants {
    public static class Responses {
        public static class codes {
            public static String SUCCESS = "00";
            public static String INSUFFICIENT_FUNDS = "51";
            public static String GENERAL_ERROR = "57";
            public static String INVALID_DR_ACCOUNT = "52";
            public static String INVALID_CR_ACCOUNT = "53";
            public static String INVALID_CUSTOMER = "01";
            public static String INVALID_AMOUNT = "13";
        }

        public static class messages {
            public static String SUCCESS = "Successful";
            public static String INSUFFICIENT_FUNDS = "Insufficient funds";
            public static String GENERAL_ERROR = "General Error";
            public static String INVALID_DR_ACCOUNT = "Invalid debit account";
            public static String INVALID_CR_ACCOUNT = "Invalid credit account";
            public static String INVALID_CUSTOMER = "Invalid customer id";
            public static String INVALID_AMOUNT = "Invalid transaction amount";
        }
    }
}

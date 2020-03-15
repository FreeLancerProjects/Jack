package com.endpoint.Jack.models;

import java.io.Serializable;
import java.util.List;

public class BankDataModel implements Serializable {

    private List<BankModel> data;

    public List<BankModel> getData() {
        return data;
    }

    public class BankModel implements Serializable
    {
        private String account_bank_name;
        private String account_number;
        private String account_IBAN;
        private String account_name;

        public String getAccount_bank_name() {
            return account_bank_name;
        }

        public String getAccount_number() {
            return account_number;
        }

        public String getAccount_IBAN() {
            return account_IBAN;
        }

        public String getAccount_name() {
            return account_name;
        }
    }
}

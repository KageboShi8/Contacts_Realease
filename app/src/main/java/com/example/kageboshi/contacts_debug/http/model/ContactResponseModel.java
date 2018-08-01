package com.example.kageboshi.contacts_debug.http.model;

import java.util.List;

public class ContactResponseModel {
    /**
     * message : Ok
     * data : {"amount":2,"contacts":[{"phone":"15816934400","name":"被装"},{"phone":"13416261920","name":"化更"}]}
     * code : 0
     */

    private String message;
    private DataBean data;
    private int code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static class DataBean {
        /**
         * amount : 2
         * contacts : [{"phone":"15816934400","name":"被装"},{"phone":"13416261920","name":"化更"}]
         */

        private int amount;
        private List<ContactsBean> contacts;

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public List<ContactsBean> getContacts() {
            return contacts;
        }

        public void setContacts(List<ContactsBean> contacts) {
            this.contacts = contacts;
        }

        public static class ContactsBean {
            /**
             * phone : 15816934400
             * name : 被装
             */

            private String phone;
            private String name;

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}

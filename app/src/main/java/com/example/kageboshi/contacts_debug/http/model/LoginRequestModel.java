package com.example.kageboshi.contacts_debug.http.model;

public class LoginRequestModel {
    /**
     * ver : 0
     * typ : 0
     * data : {"username":"xxxx","password":"md5(加料处理)","iem":"xxxxxxx"}
     */

    private int ver;
    private int typ;
    private DataBean data;

    public int getVer() {
        return ver;
    }

    public void setVer(int ver) {
        this.ver = ver;
    }

    public int getTyp() {
        return typ;
    }

    public void setTyp(int typ) {
        this.typ = typ;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * username : xxxx
         * password : md5(加料处理)
         * iem : xxxxxxx
         */

        private String username;
        private String password;
        private String iem;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getIem() {
            return iem;
        }

        public void setIem(String iem) {
            this.iem = iem;
        }
    }
//    /**
//     * ver : 0
//     * typ : 0
//     * data : {"username":"xxxx","password":"md5(加料处理)""iem":"xxxxx}
//     */
//
//    private int ver;
//    private int typ;
//    private DataBean data;
//
//    public int getVer() {
//        return ver;
//    }
//
//    public void setVer(int ver) {
//        this.ver = ver;
//    }
//
//    public int getTyp() {
//        return typ;
//    }
//
//    public void setTyp(int typ) {
//        this.typ = typ;
//    }
//
//    public DataBean getData() {
//        return data;
//    }
//
//    public void setData(DataBean data) {
//        this.data = data;
//    }
//
//    public static class DataBean {
//        /**
//         * username : xxxx
//         * password : md5(加料处理)
//         */
//
//        private String username;
//        private String password;
//        private String iem;
//
//        public String getUsername() {
//            return username;
//        }
//
//        public void setUsername(String username) {
//            this.username = username;
//        }
//
//        public String getPassword() {
//            return password;
//        }
//
//        public void setPassword(String password) {
//            this.password = password;
//        }
//
//        public String getIem() {
//            return iem;
//        }
//
//        public void setIem(String iem) {
//            this.iem = iem;
//        }
//    }

}

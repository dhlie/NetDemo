package com.fanqiecar.demo.model;

import java.util.List;

/**
 * Created by DuanHl on 2018/3/10.
 */

public class Location {
    private List<Double> queryLocation;
    private List<AddrListBean> addrList;

    public List<Double> getQueryLocation() {
        return queryLocation;
    }

    public void setQueryLocation(List<Double> queryLocation) {
        this.queryLocation = queryLocation;
    }

    public List<AddrListBean> getAddrList() {
        return addrList;
    }

    public void setAddrList(List<AddrListBean> addrList) {
        this.addrList = addrList;
    }

    public static class AddrListBean {
        /**
         * type : doorPlate
         * status : 0
         * name :
         * admCode :
         * admName :
         * nearestPoint : []
         * distance : -1
         */

        private String type;
        private int status;
        private String name;
        private String admCode;
        private String admName;
        private int distance;
        private List<?> nearestPoint;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAdmCode() {
            return admCode;
        }

        public void setAdmCode(String admCode) {
            this.admCode = admCode;
        }

        public String getAdmName() {
            return admName;
        }

        public void setAdmName(String admName) {
            this.admName = admName;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        public List<?> getNearestPoint() {
            return nearestPoint;
        }

        public void setNearestPoint(List<?> nearestPoint) {
            this.nearestPoint = nearestPoint;
        }
    }
}

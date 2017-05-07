package com.example.tripathy.sharelocation.lib.Dal;

/**
 * Created by tripathy on 12/19/2015.
 */
public class RecentContact {
        public String name;
        public long number;
        public String type;
        public String date;
        public String duration;

       public RecentContact(String name, long number, String type, String date, String duration) {
            this.name = name;
            this.number = number;
            this.type = type;
            this.date = date;
            this.duration = duration;
        }
}

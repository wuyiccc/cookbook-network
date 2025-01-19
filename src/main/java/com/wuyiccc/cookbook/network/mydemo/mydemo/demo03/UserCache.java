package com.wuyiccc.cookbook.network.mydemo.mydemo.demo03;

import io.netty.util.Recycler;

public class UserCache {

    private static final Recycler<User> userRecycler = new Recycler<User>() {
        @Override
        protected User newObject(Handle<User> handle) {

            return new User(handle);
        }
    };



    static final class User {

        private String name;

        private Recycler.Handle<User> handler;


        public void setName(String name) {

            this.name = name;
        }

        public String getName() {
            return name;
        }

        public User(Recycler.Handle<User> handler) {
            this.handler = handler;
        }

        public void recycle() {
            handler.recycle(this);
        }
    }


    public static void main(String[] args) {

        User user1 = userRecycler.get();

        user1.setName("hello");
        user1.recycle();

        User user2 = userRecycler.get();

        System.out.println(user2.getName());

        System.out.println(user1 == user2);
    }
}


package com.gxb.pojo.vo;

public class PublisherVideo {

    private UsersVO publisher;
    private boolean userLikeVideo;

    public UsersVO getPublisher() {
        return publisher;
    }

    public void setPublisher(UsersVO publisher) {
        this.publisher = publisher;
    }

    public boolean isUserLikeVideo() {
        return userLikeVideo;
    }

    public void setUserLikeVideo(boolean userLikeVideo) {
        this.userLikeVideo = userLikeVideo;
    }
}

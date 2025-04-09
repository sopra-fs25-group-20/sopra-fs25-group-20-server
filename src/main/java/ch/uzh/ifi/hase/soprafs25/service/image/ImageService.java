package ch.uzh.ifi.hase.soprafs25.service.image;

public interface ImageService {
    byte[] fetchImage();
    byte[] fetchImage(String location); // new method for location base services
}
package ch.uzh.ifi.hase.soprafs25.service.image;

public interface ImageService {
    byte[] fetchImage();
    byte[] fetchImageByLocation(String location); // new method for location base services
}
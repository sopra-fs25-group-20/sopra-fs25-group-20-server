package ch.uzh.ifi.hase.soprafs25.service.image;

public interface ImageService {
    byte[] fetchImage(double lat, double lng);
}
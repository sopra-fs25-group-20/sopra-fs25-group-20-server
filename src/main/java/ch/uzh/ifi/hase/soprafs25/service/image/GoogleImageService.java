package ch.uzh.ifi.hase.soprafs25.service.image;
import org.springframework.stereotype.Service;

@Service("googleImageService")
public class GoogleImageService implements ImageService {

    @Override
    public byte[] fetchImage(double lat, double lng) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
package ch.uzh.ifi.hase.soprafs25.service.image;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface ImageService {
    byte[] fetchImage();
    byte[] fetchImageByLocation(String location);

    CompletableFuture<byte[]> fetchImageByLocationAsync(String location);

    default List<CompletableFuture<byte[]>> fetchImagesByLocationAsync(String location, int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> fetchImageByLocationAsync(location))
                .collect(Collectors.toList());
    }
}
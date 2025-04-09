package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.service.image.ImageService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ImageControllerLocationTest {

    @Test
    public void testGetImageWithoutLocation() {
        ImageService mockService = mock(ImageService.class);
        byte[] fakeImage = new byte[]{10, 20, 30};
        when(mockService.fetchImage()).thenReturn(fakeImage);

        ImageController controller = new ImageController(mockService);
        ResponseEntity<?> response = controller.getImage(null);

        assertNotNull(response);
        assertArrayEquals(fakeImage, (byte[]) response.getBody());
        verify(mockService).fetchImage();
    }

    @Test
    public void testGetImageWithLocation() {
        ImageService mockService = mock(ImageService.class);
        byte[] fakeImage = new byte[]{40, 50, 60};
        when(mockService.fetchImage("asia")).thenReturn(fakeImage);

        ImageController controller = new ImageController(mockService);
        ResponseEntity<?> response = controller.getImage("asia");

        assertNotNull(response);
        assertArrayEquals(fakeImage, (byte[]) response.getBody());
        verify(mockService).fetchImage("asia");
    }
}

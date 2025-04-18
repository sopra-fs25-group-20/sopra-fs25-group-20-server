package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.service.image.ImageService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ImageControllerTest {

    @Test
    void testGetImageReturnsBytes() {
        ImageService mockService = mock(ImageService.class);
        byte[] fakeImage = new byte[]{1, 2, 3};
        when(mockService.fetchImage()).thenReturn(fakeImage);

        ImageController controller = new ImageController(mockService);
        ResponseEntity<?> responseEntity = controller.getImage(null);
        byte[] response = (byte[]) responseEntity.getBody();

        assertArrayEquals(fakeImage, response);
        verify(mockService).fetchImage();
    }
}

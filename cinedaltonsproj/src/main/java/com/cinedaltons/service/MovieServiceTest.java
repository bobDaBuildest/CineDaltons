import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @Mock
    private MovieRepository mockRepository; // The fake dependency

    @InjectMocks
    private MovieService movieService; // The class being tested (SUT)

    // ... (Tests follow)
    @Test
    void movieExists_shouldReturnTrue_whenMovieFound() {
        // 1. Arrange (Stubbing)
        String movieTitle = "Inception";
        Movie testMovie = new Movie(movieTitle); // Assume Movie is a simple class

        // Tell the mock what to return when its findByTitle method is called with "Inception"
        Mockito.when(mockRepository.findByTitle(movieTitle))
                .thenReturn(Optional.of(testMovie));

        // 2. Act (Call the SUT method)
        boolean result = movieService.movieExists(movieTitle);

        // 3. Assert (Verify the result)
        assertTrue(result, "The service should confirm the movie exists.");
    }
    @Test
    void movieExists_shouldCallRepositoryOnce() {
        // Arrange (Setup mock behavior)
        String movieTitle = "Avatar";
        Mockito.when(mockRepository.findByTitle(movieTitle)).thenReturn(Optional.empty());

        // Act
        movieService.movieExists(movieTitle);

        // Assert (Verification)
        // Ensure that findByTitle was called exactly once with the correct argument
        Mockito.verify(mockRepository, Mockito.times(1)).findByTitle(movieTitle);

        // Alternatively, check it was NOT called:
        // Mockito.verify(mockRepository, Mockito.never()).save(Mockito.any());
    }
}
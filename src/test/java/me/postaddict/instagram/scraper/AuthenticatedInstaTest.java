package me.postaddict.instagram.scraper;

import me.postaddict.instagram.scraper.cookie.CookieHashSet;
import me.postaddict.instagram.scraper.cookie.DefaultCookieJar;
import me.postaddict.instagram.scraper.domain.Account;
import me.postaddict.instagram.scraper.domain.Comment;
import me.postaddict.instagram.scraper.domain.Media;
import me.postaddict.instagram.scraper.domain.Tag;
import me.postaddict.instagram.scraper.interceptor.ErrorInterceptor;
import me.postaddict.instagram.scraper.interceptor.UserAgentInterceptor;
import me.postaddict.instagram.scraper.interceptor.UserAgents;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static me.postaddict.instagram.scraper.ContentCheck.*;
import static org.junit.Assert.*;

@Ignore
public class AuthenticatedInstaTest {

    private static AuthenticatedInsta client;

    @BeforeClass
    public static void setUp() throws Exception {
        Credentials credentials = new Credentials();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(loggingInterceptor)
                .addInterceptor(new UserAgentInterceptor(UserAgents.OSX_CHROME))
                .addInterceptor(new ErrorInterceptor())
                .cookieJar(new DefaultCookieJar(new CookieHashSet()))
                .build();
        client = new Instagram(httpClient);
        client.basePage();
        client.login(credentials.getLogin(), credentials.getPassword());
        client.basePage();
    }

    @Test
    public void testGetAccountByUsername() throws Exception {
        Account account = client.getAccountByUsername("kevin");
        assertEquals("kevin", account.username);
        assertTrue(checkAccount(account));
        System.out.println(account);
    }

    @Test
    public void testGetTagByName() throws Exception {
        Tag tag = client.getTagByName("corgi");
        assertEquals("corgi", tag.name);
        assertTrue(checkTag(tag));
        System.out.println(tag);
    }

    @Test
    public void testGetMedias() throws Exception {
        List<Media> mediaList = client.getMedias("kevin", 50);
        assertEquals(50, mediaList.size());
        for (Media media : mediaList) {
            assertTrue(checkMedia(media));
        }
        System.out.println(mediaList);
    }

    @Test
    public void testGetMediaByUrl() throws Exception {
        Media media = client.getMediaByUrl("https://www.instagram.com/p/BHaRdodBouH");
        assertEquals("kevin", media.owner.username);
        assertTrue(checkMedia(media));
        System.out.println(media);
    }

    @Test
    public void testGetMediaByCode() throws Exception {
        Media media = client.getMediaByCode("BHaRdodBouH");
        assertEquals("kevin", media.owner.username);
        assertTrue(checkMedia(media));
        System.out.println(media);
    }

    @Test
    public void testGetLocationMediasById() throws Exception {
        List<Media> list = client.getLocationMediasById("17326249", 13);
        assertEquals(13, list.size());
        for (Media media : list) {
            assertTrue(checkMedia(media));
        }
        System.out.println(list);
    }

    @Test
    public void testGetMediasByTag() throws Exception {
        List<Media> list = client.getMediasByTag("Moscow", 50);
        assertEquals(50, list.size());
        for (Media media : list) {
            assertTrue(checkMedia(media));
        }
        System.out.println(list);
    }

    @Test
    public void testGetTopMediasByTag() throws Exception {
        List<Media> list = client.getTopMediasByTag("Sheremetyevo");
        assertEquals(9, list.size());
        for (Media media : list) {
            assertTrue(checkMedia(media));
        }
        System.out.println(list);
    }

    @Test
    public void testGetCommentsByMediaCode() throws Exception {
        List<Comment> list = client.getCommentsByMediaCode("BHaRdodBouH", 50);
        assertEquals(50, list.size());
        for (Comment comment : list) {
            assertTrue(checkComment(comment));
        }
        System.out.println(list);
    }

    @Test
    public void testGetIdFromCode() throws Exception {
        String code = Media.getCodeFromId("1270593720437182847");
        assertEquals("BGiDkHAgBF_", code);
        code = Media.getCodeFromId("1270593720437182847_3");
        assertEquals("BGiDkHAgBF_", code);
    }

    @Test
    public void testGetCodeFromId() throws Exception {
        String id = Media.getIdFromCode("BGiDkHAgBF_");
        assertEquals("1270593720437182847", id);
    }

    @Test
    public void testPreviewComments() throws Exception {
        Media media = client.getMedias("kevin", 1).get(0);
        System.out.println(media);
        if (media.commentsCount > 0){
            assertTrue(media.previewCommentsList.size() > 0);
            for (Comment comment : media.previewCommentsList) {
                assertTrue(checkComment(comment));
            }
        } else {
            assertFalse(media.previewCommentsList.size() > 0);
        }
    }

    @Test
    public void testLikeMediaByCode() throws Exception {
        client.likeMediaByCode("PASTE_HERE_MEDIA_CODE");
    }

    @Test
    public void testUnlikeMediaByCode() throws Exception {
        client.unlikeMediaByCode("PASTE_HERE_MEDIA_CODE");
    }

    @Test
    public void testAddMediaComment() throws Exception {
        Comment comment = client.addMediaComment("PASTE_HERE_MEDIA_CODE", "PASTE_COMMENT_TEXT");
        System.out.println(comment);
    }

    @Test
    public void testDeleteMediaComment() throws Exception {
        client.deleteMediaComment("PASTE_HERE_MEDIA_CODE", "PASTE_COMMENT_ID");
    }

    @Test
    public void testFollows() throws Exception {
        Account account = client.getAccountByUsername("kevin");
        List<Account> follows = client.getFollows(account.id, 50);
        assertEquals(follows.size(), 50);
    }

    @Test
    public void testFollowers() throws Exception {
        Account account = client.getAccountByUsername("kevin");
        List<Account> followers = client.getFollowers(account.id, 50);
        assertEquals(followers.size(), 50);
    }

}

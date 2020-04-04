package kr.ac.skuniv.artsharing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kr.ac.skuniv.artsharing.domain.dto.art.ArtGetDetailDto;
import kr.ac.skuniv.artsharing.domain.dto.art.ArtGetDto;
import kr.ac.skuniv.artsharing.domain.dto.art.ArtGetPagingDto;
import kr.ac.skuniv.artsharing.service.art.ArtDeleteService;
import kr.ac.skuniv.artsharing.service.art.ArtGetService;
import kr.ac.skuniv.artsharing.service.art.ArtSaveService;
import kr.ac.skuniv.artsharing.service.art.ArtUpdateService;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.Cookie;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ArtControllerTest {

    @InjectMocks
    private ArtController artController;

    @Mock
    private ArtSaveService artSaveService;
    @Mock
    private ArtGetService artGetService;
    @Mock
    private ArtUpdateService artUpdateService;
    @Mock
    private ArtDeleteService artDeleteService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @BeforeEach
    public void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(artController)
                .alwaysDo(print()).build();
    }

    private ArtGetDto artGetDtoFixture = new EasyRandom().nextObject(ArtGetDto.class);
    private ArtGetDetailDto artGetDetailDtoFixture = new EasyRandom().nextObject(ArtGetDetailDto.class);
    private ArtGetPagingDto artGetPagingDtoFixture = new EasyRandom().nextObject(ArtGetPagingDto.class);
    private MockMultipartFile file = new MockMultipartFile("imageFile", "test.txt", null, "test data".getBytes());
    private Cookie cookie = new Cookie("user", "token");
    private byte[] bytes = new byte[10];

    @Test
    void saveArt() throws Exception {
        //given
        given(artSaveService.saveArt(any(), anyString(), any())).willReturn(artGetDtoFixture);

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/artSharing/art")
                .file(file)
                .param("json", "json")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated());
    }

//    @Test
//    void updateArt() throws Exception {
//        //given
//        given(artUpdateService.updateArt(any(), any(), anyString())).willReturn(artGetDtoFixture);
//
//        mockMvc.perform(
//                MockMvcRequestBuilders.multipart("/artSharing/art/update")
//                        .file(file)
//                        .param("json", "json")
//                        .cookie(cookie)
//                        .contentType(MediaType.APPLICATION_JSON)
//        )
//                .andExpect(status().isOk());
//    }

    @Test
    void deleteArt() throws Exception {
        //given
        given(artDeleteService.deleteArt(any(), anyLong())).willReturn(ResponseEntity.noContent().build());

        mockMvc.perform(
                delete("/artSharing/art/{art_id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        )
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllArts() throws Exception {
        given(artGetService.getAllArts(anyInt())).willReturn(artGetPagingDtoFixture);

        mockMvc.perform(
                get("/artSharing/art/list")
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageNo", String.valueOf(1))
        )
                .andExpect(status().isOk());
    }

    @Test
    void getArtsByUserId() throws Exception {
        given(artGetService.getArtsByUserId(any(), anyInt())).willReturn(artGetPagingDtoFixture);

        mockMvc.perform(
                get("/artSharing/art")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageNo", String.valueOf(1))
        )
                .andExpect(status().isOk());
    }

    @Test
    void getArtsByArtist() throws Exception {
        given(artGetService.getArtsByArtist(anyString(), anyInt())).willReturn(artGetPagingDtoFixture);

        mockMvc.perform(
                get("/artSharing/art/{artistId}", "userID")
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageNo", String.valueOf(1))
        )
                .andExpect(status().isOk());
    }

    @Test
    void getArtDetail() throws Exception {
        given(artGetService.getArtDetail(anyLong())).willReturn(artGetDetailDtoFixture);

        mockMvc.perform(
                get("/artSharing/art/detail/{art_id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());
    }

    @Test
    void searchArtByKeyword() throws Exception {
        given(artGetService.searchArtByKeyword(anyString(), anyInt())).willReturn(artGetPagingDtoFixture);

        mockMvc.perform(
                get("/artSharing/art/search/{keyword}", "keyword")
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageNo", String.valueOf(1))
        )
                .andExpect(status().isOk());
    }

    @Test
    void getImage() throws Exception {
        given(artGetService.getImageResource(anyLong())).willReturn(bytes);

        mockMvc.perform(
                get("/artSharing/art/image/{art_id}", 1L)
                .contentType(MediaType.IMAGE_JPEG_VALUE)
        )
                .andExpect(status().isOk());
    }
}
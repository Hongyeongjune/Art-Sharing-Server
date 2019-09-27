package kr.ac.skuniv.artsharing.service.art;

import kr.ac.skuniv.artsharing.domain.dto.art.ArtUpdateDto;
import kr.ac.skuniv.artsharing.domain.entity.Art;
import kr.ac.skuniv.artsharing.domain.entity.ArtImage;
import kr.ac.skuniv.artsharing.exception.UserDefineException;
import kr.ac.skuniv.artsharing.repository.ArtImageRepository;
import kr.ac.skuniv.artsharing.repository.ArtRepository;
import kr.ac.skuniv.artsharing.service.CommonService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Service
public class ArtUpdateService {

    private final CommonService commonService;
    private final ArtRepository artRepository;
    private final ArtImageRepository artImageRepository;
    private final ArtSaveService artSaveService;

    public ArtUpdateService(CommonService commonService, ArtRepository artRepository,
                            ArtImageRepository artImageRepository, ArtSaveService artSaveService) {
        this.commonService = commonService;
        this.artRepository = artRepository;
        this.artImageRepository = artImageRepository;
        this.artSaveService = artSaveService;
    }

    /**
     * 작품 수정
     * @param request : userId를 조회하기 위한 HttpServletRequest 객체
     * @param artUpdateDto : 작품을 수정할 데이터
     */
    public Art updateArt(MultipartFile imageFile, HttpServletRequest request, ArtUpdateDto artUpdateDto) throws IOException {
        String userId = commonService.getUserIdByToken(request);

        if(userId == null)
            throw new UserDefineException("로그인이 필요합니다.");

        Art art = artRepository.findByMemberIdAndId(userId, artUpdateDto.getId());

        if(!userId.equals(art.getMember().getId())){
            throw new UserDefineException("작품을 수정할 권한이 없습니다.");
        }

        if(imageFile != null){
            updateArtImage(imageFile, artUpdateDto.getId());
        }

        art.updateArt(artUpdateDto);

        return artRepository.save(art);
    }

    private void updateArtImage(MultipartFile imageFile, Long artNo) throws IOException {
        Art art = artRepository.findById(artNo).get();
        ArtImage artImage = artImageRepository.findByArt(art).get();
        ArtImage updateArtImage = artSaveService.saveImage(imageFile, art);

        artImage.updateArtImage(updateArtImage);
        artImageRepository.save(artImage);
    }
}
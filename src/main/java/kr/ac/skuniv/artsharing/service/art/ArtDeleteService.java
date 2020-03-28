package kr.ac.skuniv.artsharing.service.art;

import kr.ac.skuniv.artsharing.domain.entity.Art;
import kr.ac.skuniv.artsharing.domain.entity.Member;
import kr.ac.skuniv.artsharing.exception.UserDefineException;
import kr.ac.skuniv.artsharing.repository.ArtRepository;
import kr.ac.skuniv.artsharing.service.CommonService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;

@Service
public class ArtDeleteService {

    private final CommonService commonService;
    private final ArtRepository artRepository;

    public ArtDeleteService(CommonService commonService, ArtRepository artRepository) {
        this.commonService = commonService;
        this.artRepository = artRepository;
    }

    /**
     * 작품 삭제
     * @param cookie : userId를 조회하기 위한 Cookie 객체
     * @param id : 수정할 작품의 번호
     */
    public ResponseEntity deleteArt(Cookie cookie, Long id) {
        Member member = commonService.getMemberByCookie(cookie);

        Art art = artRepository.findById(id)
                .orElseThrow(()->new UserDefineException("해당 작품을 찾을 수 없습니다."));

        commonService.checkMember(member.getId(), art.getMember().getId());

        artRepository.delete(art);

        return ResponseEntity.noContent().build();
    }
}

package kr.ac.skuniv.artsharing.service.rent;

import kr.ac.skuniv.artsharing.domain.dto.rent.RentGetDto;
import kr.ac.skuniv.artsharing.domain.dto.rent.RentGetPagingDto;
import kr.ac.skuniv.artsharing.domain.entity.art.Art;
import kr.ac.skuniv.artsharing.domain.entity.member.Member;
import kr.ac.skuniv.artsharing.domain.roles.MemberRole;
import kr.ac.skuniv.artsharing.exception.UserDefineException;
import kr.ac.skuniv.artsharing.exception.art.ArtNotFoundException;
import kr.ac.skuniv.artsharing.exception.rent.RentNotFoundException;
import kr.ac.skuniv.artsharing.repository.art.ArtRepository;
import kr.ac.skuniv.artsharing.repository.rent.RentRepository;
import kr.ac.skuniv.artsharing.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;

@Service
@RequiredArgsConstructor
public class RentGetService {

    private final RentRepository rentRepository;
    private final CommonService commonService;
    private final ArtRepository artRepository;

    /**
     * 작가가 본인의 작품의 대여기록을 조회
     * @param cookie : 사용자 정보
     * @param art_id : 작품 번호
     * @param pageNo : 페이지 번호
     * @return : 대여 기록
     */
    @Transactional(readOnly = true)
    public RentGetPagingDto getRentByArt(Cookie cookie, Long art_id, int pageNo) {
        Member member = commonService.getMemberByCookie(cookie);

        Art art = artRepository.findById(art_id).orElseThrow(ArtNotFoundException::new);

        commonService.checkAuthority(member.getUserId(), art.getMember().getUserId());

        Page<RentGetDto> rentPage = rentRepository.findRentByArt(art_id, pageNo);

        if(rentPage == null) {
            throw new RentNotFoundException();
        }

        return RentGetPagingDto.of(rentPage);
    }

    /**
     * 고객이 자신의 대여기록을 조회
     * @param cookie : 고객 정보
     * @param pageNo : 페이지 번호
     * @return : 대여 기록
     */
    @Transactional(readOnly = true)
    public RentGetPagingDto getRent(Cookie cookie, int pageNo) {
        Member member = commonService.getMemberByCookie(cookie);

        Page<RentGetDto> rentPage = rentRepository.findRentByMember(member.getUserId(), pageNo);

        if(rentPage == null) {
            throw new RentNotFoundException();
        }

        return RentGetPagingDto.of(rentPage);
    }

}

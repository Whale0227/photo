package com.photo.module.share.service;

import com.photo.module.share.dto.CreateShareDTO;
import com.photo.module.share.vo.ShareVO;

import java.util.List;

public interface ShareService {

    /** 创建分享链接 */
    ShareVO createShare(CreateShareDTO dto);

    /** 访问分享（公开接口，无需登录） */
    Object accessShare(String shareCode, String extractCode);

    /** 获取我的分享列表 */
    List<ShareVO> listMyShares();

    /** 取消分享 */
    void cancelShare(Long shareId);
}

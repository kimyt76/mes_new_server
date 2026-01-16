<div style="max-width: 700px; margin: 10px auto 20px;">
    <div>
        <div style="display:inline-block;margin: 0px auto;">
            <table style=" margin: 0 auto; border-collapse: collapse; background-color: #fff; border-radius:10px;box-shadow:5px 5px 10px rgba(0,0,0,0.2);">
                <!--header-->
                <tbody>
                <tr>
                    <td style="padding: 10px; border-bottom: 1px solid #e5e5e5;">
                        <table style="width: 100%; border-collapse: collapse;">
                            <tbody>
                            <tr>
                                <td style="width: 140px;">
                                    <img src="https://logincd.ecounterp.com/MemberInfo/Logo/_64301_589b0388302ae48faf7de525df752bba_logotop.gif">
                                </td>
                                <td style="padding-left: 10px; font: 14px/1.6 Arial; color: #000; font-weight: bold; text-align: right;">
                                    발주서
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
                <!--//header-->
                <!--Contents-->
                <tr>
                    <td style="padding: 0 20px;">
                        <div style="margin: 20px 0 0;">
                            <!--양식설정-->
                            <table style=" width:100%;border-collapse: collapse; word-break: break-all; box-sizing: border-box;">
                                <tbody>
                                <tr>
                                    <td style="font-family: Arial; font-size: 16px;font-weight:bold;color:#0775a6;">
                                        수신 : ${vo.customerName}
                                    </td>
                                </tr>
                                <tr>
                                    <td style="font-family: Arial; font-size: 22px;font-weight:bold;color:#000;">
                                        (주)진코스텍으로부터 발주서(${vo.orderNo})가 도착했습니다.
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 10px 0 0px; font-family: Arial; font-size: 16px; color: #000; font-weight: bold; border-bottom: 2px solid #000;">
                                        발신정보
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 10px 0; border-bottom: 1px solid #999;">
                                        <table
                                                style="width: 100%; border-collapse: collapse; table-layout: fixed; box-sizing: border-box;">
                                            <tbody>
                                            <tr>
                                                <td style="padding: 5px; box-sizing: border-box; font-family: Arial; font-size: 14px; line-height: 1.6; color: #000; width: 120px; ">
                                                    보낸회사
                                                </td>
                                                <td style="padding: 5px; box-sizing: border-box; font-family: Arial; font-size: 14px; line-height: 1.6; color: #000; font-weight:bold">
                                                    (주)진코스텍
                                                </td>
                                            </tr>
                                            <tr>
                                                <td style="padding: 5px; box-sizing: border-box; font-family: Arial; font-size: 14px; line-height: 1.6; color: #000; width: 120px; ">
                                                    발행일자</td>
                                                <td style="padding: 5px; box-sizing: border-box; font-family: Arial; font-size: 14px; line-height: 1.6; color: #000; font-weight:bold">
                                                    ${vo.orderDate}
                                                </td>
                                            </tr>
                                            <tr>
                                                <td style="padding: 5px; box-sizing: border-box; font-family: Arial; font-size: 14px; line-height: 1.6; color: #000; width: 120px; ">
                                                    Email
                                                </td>
                                                <td style="padding: 5px; box-sizing: border-box; font-family: Arial; font-size: 14px; line-height: 1.6; color: #000; font-weight:bold">
                                                    ${vo.senderEmail}
                                                </td>
                                            </tr>
                                            <tr>
                                                <td style="padding: 5px; box-sizing: border-box; font-family: Arial; font-size: 14px; line-height: 1.6; color: #000; width: 120px; ">
                                                    연락처
                                                </td>
                                                <td style="padding: 5px; box-sizing: border-box; font-family: Arial; font-size: 14px; line-height: 1.6; color: #000; font-weight:bold">
                                                    031-599-7118
                                                </td>
                                            </tr>
                                            <tr>
                                                <td style="padding: 5px; box-sizing: border-box; font-family: Arial; font-size: 14px; line-height: 1.6; color: #000; width: 120px; ">
                                                    메모</td>
                                                <td style="padding: 5px; box-sizing: border-box; font-family: Arial; font-size: 14px; line-height: 1.6; color: #000; font-weight:bold">
                                                    ${vo.memo}
                                                </td>
                                            </tr>
                                            </tbody>
                                        </table>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                            <!--//양식설정-->
                        </div>
                    </td>
                </tr>
                <!--//Contents-->
                <!--footer-->
                <tr>
                    <td style="padding: 10px 20px; background: #f8f8f8; border-top: 1px solid #e5e5e5;border-radius:0 0 10px 10px">
                        <table style="width: 100%; border-collapse: collapse;">
                            <tbody>
                            <tr>
<#--                                <td style="width: 160px; text-align: center">-->
<#--                                    <img src="https://login.ecounterp.com/static/contents/images/ci/ecount-ci-sendmail-footer.png">-->
<#--                                </td>-->
                                <td style="text-align: center; padding-left: 1px; font: 12px/1.6 Arial; color: #666;">
                                    <span style="font-weight: bold;">(주)진코스텍 - 최고 품질의 화장품 패치류 전문 ODM/OEM 기업</span><br>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
                <!--//footer-->
                </tbody>
            </table>
        </div>
    </div>
</div>

package kr.co.greenart.coffee;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class outputControl {
	private static ObjectMapper mapper = new ObjectMapper();
	private final CoffeeDAO dao = new CoffeeDAO();
	private String pattern = "^[0-9]*$";

	public outputControl() {
	};

	public static void makeErrorMessage(int errorCode, String errorMessage, HttpServletResponse resp)
			throws IOException {
		ObjectNode errorNode = mapper.createObjectNode();
		errorNode.put("message", errorMessage);
		errorNode.put("callNumber", "010-5039-5692");
		String errorMes = mapper.writeValueAsString(errorNode);

		resp.setStatus(errorCode);
		resp.setHeader("Content-Type", "application/json;charset=utf-8");
		resp.getWriter().println(errorMes);
	}

	public static void makePassMessage(int code, String json, HttpServletResponse resp) throws IOException {
		resp.setStatus(200);
		resp.setHeader("Content-Type", "application/json;charset=utf-8");
		resp.getWriter().println(json);
	}

	public void progressCoffee(String reqParam, HttpServletResponse resp) throws IOException {
		// 쿼리파라미터가 존재하지 않는 경우
		if (reqParam == null) {
			try {
				List<Coffee> list = dao.getAll();
				String json = mapper.writeValueAsString(list);
				outputControl.makePassMessage(200, json, resp);
			} catch (SQLException | JsonProcessingException e) {
				e.printStackTrace();
				outputControl.makeErrorMessage(500, "서버 요청 처리 중 에러가 발생했습니다.", resp);
			}
			// 쿼리파라미터 존재할 경우 숫자로만 이루어져 있는지 확인
		} else if (Pattern.matches(pattern, reqParam)) {
			// 숫자이고 목록에 존재하는 경우
			if (dao.isExist(reqParam)) {
				try {
					Coffee coffee = dao.getRowByNumber(Integer.valueOf(reqParam));
					String json = mapper.writeValueAsString(coffee);
					outputControl.makePassMessage(200, json, resp);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (SQLException | JsonProcessingException e) {
					e.printStackTrace();
				}
				// 숫자이지만 목록에 없는 경우
			} else {
				outputControl.makeErrorMessage(404, "목록에 존재하지 않습니다.", resp);
			}
			// 숫자가 아닐 경우
		} else {
			outputControl.makeErrorMessage(400, "숫자를 입력해주세요", resp);
		}
	}
}

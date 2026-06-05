package com.gemimah.nestartertemplate.util;

import com.gemimah.nestartertemplate.dto.PageResponse;
import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public final class PaginationUtil {

	private PaginationUtil() {
	}

	public static <E, D> PageResponse<D> map(Page<E> page, Function<E, D> mapper) {
		return PageResponse.from(page.map(mapper));
	}

	public static Pageable defaultPageable(Pageable pageable) {
		return pageable;
	}
}

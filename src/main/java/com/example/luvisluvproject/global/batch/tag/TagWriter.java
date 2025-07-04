package com.example.luvisluvproject.global.batch.tag;

import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.example.luvisluvproject.domain.tag.entity.Tag;
import com.example.luvisluvproject.domain.tag.repository.TagJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TagWriter implements ItemWriter<Tag> {

	private final TagJpaRepository tagJpaRepository;

	@Override
	public void write(Chunk<? extends Tag> chunk) throws Exception {
		List<? extends Tag> tagList = chunk.getItems();
		tagJpaRepository.saveAll(tagList);
	}
}

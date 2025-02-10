package com.korea.shop.service;

import com.korea.shop.domain.item.Item;
import com.korea.shop.repository.ItemRepositoryClass;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepositoryClass itemRepository;

    // 회원 가입
    @Transactional
    public Long join(Item item){
        itemRepository.save(item);
        return item.getId();
    }


    // 회원 전체조회
    public List<Item> findItems(){

        return itemRepository.findAll();
    }

    // 회원 1명조회
    public Item findOne(Long itemId){

        Item result = itemRepository.findOne(itemId);

        return result;
    }
}

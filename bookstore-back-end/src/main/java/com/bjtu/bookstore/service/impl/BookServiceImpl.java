package com.bjtu.bookstore.service.impl;

import com.bjtu.bookstore.entity.Book;
import com.bjtu.bookstore.entity.Cart;
import com.bjtu.bookstore.mapper.BookMapper;
import com.bjtu.bookstore.mapper.CategoryMapper;
import com.bjtu.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public HashMap<String, Object> getDetail(Book book) {

        ArrayList<Book> books = new ArrayList<>();
        books = bookMapper.getDetail(book.getId());
        for(int i=0;i<books.size();i++) {
            books.get(i).setCategoryName(categoryMapper.getNameById(books.get(i).getCategoryId()));
        }
        HashMap<String, Object> data = new HashMap<>();
        data.put("bookDetail", books);
        return data;
    }

    @Override
    public HashMap<String, Object> getAllBooks(Cart cart) {
        HashMap<String, Object> datas = new HashMap<>();
        String categoryId=categoryMapper.getIdByName(cart.getCartId());
        ArrayList<Book> books;
        Book b1=cart.getBooks().get(0);

        if(categoryId==null) {
            books=bookMapper.getSomePageBook(b1.getStartNum()-1,10);
            datas.put("allNum",bookMapper.getAllNum());
        }
        else {
            books=bookMapper.getSomePageBookByCategory(b1.getStartNum()-1,10, categoryId);
            datas.put("allNum",bookMapper.getAllNumByCategory(categoryId));
        }

        for(int i=0;i<books.size();i++) {
            books.get(i).setCategoryName(categoryMapper.getNameById(books.get(i).getCategoryId()));
        }

        datas.put("bookList", books);

        return datas;
    }

    @Override
    public HashMap<String, Object> search(Cart cart) {
        HashMap<String, Object> datas = new HashMap<>();
        Book b1=cart.getBooks().get(0);
        String content="%";
        content+=b1.getName();
        content+="%";
        ArrayList<Book> books=bookMapper.search(b1.getStartNum()-1,10,content);

        for(int i=0;i<books.size();i++) {
            books.get(i).setCategoryName(categoryMapper.getNameById(books.get(i).getCategoryId()));
        }

        datas.put("bookList", books);
        datas.put("allNum",bookMapper.getAllNumByContent(b1.getName()));
        return datas;
    }

    @Override
    public void shaixuan(Cart cart) {
        bookMapper.updateState(cart.getBooks().get(0).getId());
    }
}

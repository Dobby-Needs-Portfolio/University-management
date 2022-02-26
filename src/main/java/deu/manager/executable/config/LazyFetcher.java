package deu.manager.executable.config;

import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Lazy Fetch를 위한 클래스입니다.
 * @param <Key> key값의 타입
 * @param <Data> Database에서 받아올 데이터의 타입
 */
public class LazyFetcher<Key, Data> {
    /**
     * Fetch 후 데이터를 저장하는 변수
     */
    private Data data;

    /**
     * SQL query에 필요한 key
     */
    private final Key key;

    /**
     * query execution을 담당할 함수
     */
    private final Function<Key, Data> sqlLambda;

    /**
     * Constructor
     * @param key db에 검색될 키(List<Key> 타입 추천)
     * @param fetchLambda sql을 수행할 람다 표현식
     */
    public LazyFetcher(Key key, Function<Key, Data> fetchLambda){
        this.key = key;
        this.sqlLambda = fetchLambda;
    }

    /**
     * 데이터를 꺼내는 메소드. 처음 데이터를 꺼내는 시점일 경우, db에서 데이터를 fetch해옴.
     * 만약 이전에 이미 호출이 있었다면, 저장된 데이터를 꺼내서 보여줌.
     * @return data that fetched from database(before if called twice or more)
     */
    public Data get(){
        if (Objects.isNull(data)){
            data = sqlLambda.apply(key);
        }
        return data;
    }
}
package me.powerarc.taketogether.route;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import me.powerarc.taketogether.common.MethodExecutionTime;
import me.powerarc.taketogether.location.Location;
import me.powerarc.taketogether.restapi.Kakao;
import me.powerarc.taketogether.restapi.Skt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final Skt skt;
    private final Kakao kakao;

    @MethodExecutionTime
    public List<Route> getRoutes(Location start, Location end) throws InterruptedException {
        List<Route> list = new ArrayList<>();
        Thread threadSkt = new Thread(() -> {
            try {
                list.add(skt.searchRoute(start, end));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });

        Thread threadKakao = new Thread(() -> {
            list.add(kakao.searchRoute(start, end));
        });

        threadKakao.start();
        threadSkt.start();

        threadKakao.join();
        threadSkt.join();
        return list;
    }

    @MethodExecutionTime
    public List<Route> getRoutesWithoutThread(Location start, Location end) throws JsonProcessingException {
        List<Route> list = new ArrayList<>();
        list.add(skt.searchRoute(start, end));
        list.add(kakao.searchRoute(start, end));
        return list;
    }

}

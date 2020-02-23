package com.tistory.johnmarc.todo.handler

import com.tistory.johnmarc.todo.domain.Status
import com.tistory.johnmarc.todo.domain.Todo
import com.tistory.johnmarc.todo.domain.TodoRepository
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.notFound
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.json
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

@Component
class TodoHandler(private val repo: TodoRepository) {
    fun getAll(req: ServerRequest): Mono<ServerResponse> = ok()
            .json()
            .body(Mono.just(repo.findAll()), Todo::class.java)
            .switchIfEmpty(notFound().build())

    fun getById(req: ServerRequest): Mono<ServerResponse> = ok()
            .json()
            .body(Mono.just(repo.findById(req.pathVariable("id").toLong()).get()), Todo::class.java)
            .switchIfEmpty(notFound().build())

    fun save(req: ServerRequest): Mono<ServerResponse> = ok()
            .json()
            .body(req.bodyToMono(Todo::class.java)
                    .switchIfEmpty(Mono.empty())
                    .filter(Objects::nonNull)
                    .flatMap { todo ->
                        Mono.fromCallable {
                            repo.save(todo)
                        }.then(Mono.just(todo))
                    }
            ).switchIfEmpty(notFound().build())

    fun status(req: ServerRequest): Mono<ServerResponse> = ok()
            .json()
            .body(Mono.justOrEmpty(repo.findById(req.pathVariable("id").toLong()))
                    .switchIfEmpty(Mono.empty())
                    .filter(Objects::nonNull)
                    .flatMap { todo ->
                        Mono.fromCallable {
                            todo.status = Status.valueOf(req.pathVariable("status"))
                            todo.updatedAt = LocalDateTime.now()
                            repo.save(todo)
                        }.then(Mono.just(todo))
                    }).switchIfEmpty(notFound().build())

    fun delete(req: ServerRequest): Mono<ServerResponse> = ok()
            .json()
            .body(Mono.justOrEmpty(repo.findById(req.pathVariable("id").toLong()))
                    .switchIfEmpty(Mono.empty())
                    .filter(Objects::nonNull)
                    .flatMap { todo ->
                        Mono.fromCallable {
                            repo.delete(todo)
                        }.then(Mono.just(todo))
                    }).switchIfEmpty(notFound().build())
}

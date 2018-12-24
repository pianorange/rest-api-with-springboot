package me.purefire.restapiwithspring.common;

import me.purefire.restapiwithspring.index.IndexController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Error発生時、IndexページにつなげてくれるためのResource
 */
public class ErrorsResource extends Resource<Errors> {

    public ErrorsResource(Errors content, Link... links) {
        super(content, links);
        //IndexController のindex()メソッドにつながるLinkをindexというRelationで設定する
        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }
}

package com.mygdx.game;

import static com.mygdx.game.User.getFeedCount;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;
import java.util.Locale;

public class Feeder extends Actor {
    TextureRegion feederImg;
    Dialog feedMenu;
    Skin skin;
    TextButton[] feedSelect;
    Boolean isEmpty;
    String name;
    String description;
    Integer price;

    public Feeder(int feederId) {
        FileHandle feederdata = Gdx.files.internal("feeder.json");
        HashMap<String, TextureRegion> images = Sprites.getImages();
        skin = new Skin(Gdx.files.internal("earthskin-ui/earthskin.json"));
        isEmpty = feederId == 0;

        JsonReader jsonRead = new JsonReader();
        JsonValue feederjson = jsonRead.parse(feederdata);
        JsonValue feeder = getFeederData(feederjson,feederId);
        feederImg = images.get(feeder.getString("spriteName"));
        name = feeder.getString("name");
        description = feeder.getString("description");
        price = feeder.getInt("price");

        setX(125);
        setY(130);
        setWidth(feederImg.getRegionWidth());
        setHeight(feederImg.getRegionHeight());
        setBounds(getX(),getY(),getWidth(),getHeight());

        addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                getFeedMenu().show(getStage());
                return true;
            }
        });

    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(feederImg, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    private JsonValue getFeederData(JsonValue jsonValue, int id){
        JsonValue data = null;
        for(JsonValue value : jsonValue){
            int i = value.getInt("id");
            if(i==id){
                data = value;
                break;
            }
        }
        return data;
    }

    private Dialog getFeedMenu(){
        feedMenu = new Dialog("Select Feed",skin);
        feedMenu.setWidth(getStage().getWidth());
        feedMenu.setHeight(getStage().getHeight()-100);
        feedMenu.align(Align.center);

        feedSelect = new TextButton[6];

        for(int i = 1; i < 5; i++){
            int feedId = i;
            Feeder feeder = new Feeder(feedId);
            int feedCount = getFeedCount(feedId);

            feedSelect[feedId] = new TextButton(
                    String.format(
                            Locale.getDefault(), "%s x%d", feeder.name,feedCount),skin
            );

            feedSelect[feedId].addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (feedCount > 0) { //TODO: isEmpty condition
                        User.subFeedOne(feedId);
                        feedSelect[feedId].setText(
                                String.format(Locale.getDefault(),
                                        "%s x%d",feeder.name,getFeedCount(feedId)));
                        getStage().addActor(new Feeder(feedId));
                        addAction(Actions.removeActor());
                        feedMenu.remove();
                    }
                }
            });
            feedMenu.getContentTable().add(feedSelect[feedId]).row();
        }

        feedMenu.button("Close").row();
        return feedMenu;
    }

}



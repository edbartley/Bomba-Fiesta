#include "HelloWorldScene.h"
#include "SimpleAudioEngine.h" 

USING_NS_CC;
 
int SCREEN_WIDTH = 640;
int SCREEN_HEIGHT = 800;
int RISING = 0;
int FALLING = 1;
int READY = 2;

CCScene* HelloWorld::scene()
{
	// 'scene' is an autorelease object
	CCScene *scene = CCScene::node();
	
	// 'layer' is an autorelease object
	HelloWorld *layer = HelloWorld::node();

	// add layer as a child to scene
	scene->addChild(layer);

	// return the scene
	return scene;
}

// on "init" you need to initialize your instance
bool HelloWorld::init()
{
	//////////////////////////////
	// 1. super init first
	if ( !CCLayer::init() )
	{
		return false;
	}

    srand(42);
	/////////////////////////////
	// 2. add a menu item with "X" image, which is clicked to quit the program
	//    you may modify it.
//    CCDirector::sharedDirector()->setContentScaleFactor(0.78125f);
 
    mScore = 0;
        CCSize size = CCDirector::sharedDirector()->getWinSize();
	// add a "close" icon to exit the progress. it's an autorelease object
	CCMenuItemImage *pCloseItem = CCMenuItemImage::itemFromNormalImage(
										"CloseNormal.png",
										"CloseSelected.png",
										this,
										menu_selector(HelloWorld::menuCloseCallback) );
	pCloseItem->setPosition( ccp(size.width - 20, 20) );

	// create menu, it's an autorelease object
	CCMenu* pMenu = CCMenu::menuWithItems(pCloseItem, NULL);
	pMenu->setPosition( CCPointZero );
	this->addChild(pMenu, 10);

	/////////////////////////////
	// 3. add your codes below...
    CCSpriteFrameCache *frameCache = CCSpriteFrameCache::sharedSpriteFrameCache();

    frameCache->addSpriteFramesWithFile("bomba_fiesta_atlas__4444.plist", "bomba_fiesta_atlas__4444.pvr");


    mSpriteBackground = CCSprite::spriteWithSpriteFrame(frameCache->spriteFrameByName("background.png"));
    mSpriteBackground->setAnchorPoint(CCPointZero);
    this->addChild(mSpriteBackground, -1);
    mSpriteBackground->retain();

    mSeed = CCSprite::spriteWithSpriteFrame(frameCache->spriteFrameByName("seed.png"));
    mSeed->setPosition(ccp(-100, 0));
    this->addChild(mSeed);
    mSeed->retain();
    
	// add a label shows "Hello World"
	// create and initialize a label
    mScoreText = CCLabelTTF::labelWithString("0", "Arial", 24);
    mScoreText->setAnchorPoint(CCPointZero);
	mScoreText->setPosition( ccp(10, size.height - 50) );
	this->addChild(mScoreText, 10);
    mScoreText->retain();

    mPepper = new Pepper(this);

    mPepper->mCurrentPepper->setPosition(ccp((size.width - mPepper->mCurrentPepper->boundingBox().size.width) /2, 0));

    this->schedule(schedule_selector(HelloWorld::update));

    this->setIsTouchEnabled(true);


    this->schedule(SEL_SCHEDULE(&HelloWorld::launchTimer), 0.8f);
    this->schedule(SEL_SCHEDULE(&HelloWorld::fireTimer), 0.1f);

    CocosDenshion::SimpleAudioEngine::sharedEngine()->setBackgroundMusicVolume(0.5f);
    CocosDenshion::SimpleAudioEngine::sharedEngine()->playBackgroundMusic("music.wav", true);
	return true;
}



void HelloWorld::addScore(int points)
{
    string strText, strFinal;

    mScore += points;
    int tempScore = mScore;

    if(tempScore == 0)
    {
        strText = "0";
    }
    else
    {
        while(tempScore > 0)
        {
            strText += tempScore % 10 + 48;
            tempScore /= 10;
        }
        
        // Flip the string
        for(int i=0; i < strText.length();i++)
            strFinal += strText[strText.length() - i - 1];
    }

    mScoreText->setString(strFinal.c_str());
}

void HelloWorld::update(ccTime delta)
{
    // do seed collision with bombas
    for(int index = 0; index < bombaSprites.count(); ++index)
    {
        CCSprite* bomba = bombaSprites.getObjectAtIndex(index);

        if(bomba->getParent() != NULL && CCRect::CCRectIntersectsRect(bomba->boundingBox(), mSeed->boundingBox()))
        {
            // Remove the seed
            mSeed->cleanup();
            mSeed->setPosition(ccp(-100, -100));

            // update score
            addScore(5);

            // play sound
            CocosDenshion::SimpleAudioEngine::sharedEngine()->playEffect("pop.wav");  

            // Handle the candy
            CCArray *children = bomba->getChildren();
            if(children != NULL && children->count() > 0)
            {
                CCSprite *candy = (CCSprite*)children->objectAtIndex(0);
                CCPoint worldPoint = candy->convertToWorldSpace(candy->getPosition());
                candy->setTag(FALLING);
                candy->removeFromParentAndCleanup(true);

                this->addChild(candy);
                candy->setPosition(worldPoint);

                CCActionInterval* move_ease_out =
                    CCEaseSineIn::actionWithAction(CCMoveBy::actionWithDuration(1, CCPointMake(0,-1124)) );

                candy->runAction(move_ease_out);
            }

            bomba->removeFromParentAndCleanup(true);
        }
    }

    // do candy collision with pepper
    for(int index = 0; index < candySprites.count(); ++index)
    {
        CCSprite *candy = candySprites.getObjectAtIndex(index);

        if(candy->getTag() == FALLING && CCRect::CCRectIntersectsRect(candy->boundingBox(), mPepper->mCurrentPepper->boundingBox()))
        {
            // Add score
            addScore(10);

            // Play sound
            CocosDenshion::SimpleAudioEngine::sharedEngine()->playEffect("candy.wav");  

            // Remove candy
            candy->setTag(READY);
            candy->removeFromParentAndCleanup(true);
        }
    }
}

void HelloWorld::fireTimer(ccTime delta)
{
    static ccTime chiliTiming = 0;
    chiliTiming += delta;
			
	switch (mPepper->state)
	{
    case Pepper::FIRING:
		if(chiliTiming > 0.5f)
		{
	//		chiliSprite.setRegion(chiliRegions[GameScreen.CHILI_LOAD]);
            mPepper->setState(Pepper::RELOADING);//chiliState = ChiliState.LOADING;
			chiliTiming = 0;
	//		isSeedSpawned = false;
		}
		break;
    case Pepper::RELOADING:
		if(chiliTiming > 0.5f)
		{
            mPepper->setState(Pepper::FIRING);//chiliState = ChiliState.FIRING;
			chiliTiming = 0;
            float startX = mPepper->mCurrentPepper->getPosition().x +  mPepper->mCurrentPepper->boundingBox().size.width/2 + 12;
            float startY = mPepper->mCurrentPepper->boundingBox().size.height;

            mSeed->setPosition(ccp(startX, startY));

            mSeed->runAction(CCMoveTo::actionWithDuration(0.5f, ccp(startX, 1124)));

            CocosDenshion::SimpleAudioEngine::sharedEngine()->playEffect("fire.wav");    
		}
		break;
	case Pepper::HAPPY:
		if(chiliTiming > 1)
		{
//			chiliSprite.setRegion(chiliRegions[GameScreen.CHILI_LOAD]);
            mPepper->setState(Pepper::RELOADING);//chiliState = ChiliState.LOADING;
			chiliTiming = 0;
		}
		break;
	}
}

void HelloWorld::launchTimer(ccTime delta)
{
    CCSprite* bomba = getBombaSprite();

    this->addChild(bomba);

    // Pick a ballon color
    int color = rand() % 7;

    switch(color)
    {
    case 0: // black
        bomba->setDisplayFrame(CCSpriteFrameCache::sharedSpriteFrameCache()->spriteFrameByName("bomba_black.png"));
        break;
    case 1: // orange
        bomba->setDisplayFrame(CCSpriteFrameCache::sharedSpriteFrameCache()->spriteFrameByName("bomba_orange.png"));
        break;
    case 2: // pink
        bomba->setDisplayFrame(CCSpriteFrameCache::sharedSpriteFrameCache()->spriteFrameByName("bomba_purple.png"));
        break;
    case 3: // blue
        bomba->setDisplayFrame(CCSpriteFrameCache::sharedSpriteFrameCache()->spriteFrameByName("bomba_blue.png"));
        break;
    case 4: // yellow
        bomba->setDisplayFrame(CCSpriteFrameCache::sharedSpriteFrameCache()->spriteFrameByName("bomba_yellow.png"));
        break;
    case 5: // green
        bomba->setDisplayFrame(CCSpriteFrameCache::sharedSpriteFrameCache()->spriteFrameByName("bomba_green.png"));
        break;
    case 6: // red
        bomba->setDisplayFrame(CCSpriteFrameCache::sharedSpriteFrameCache()->spriteFrameByName("bomba_red.png"));
        break;
    }

    CCSize screen = CCDirector::sharedDirector()->getWinSize();
    CCSize spriteSize = bomba->boundingBox().size;

    // Pick a position
    int screenMax = (int)screen.width - spriteSize.width/2;
    int screenMin = spriteSize.width/2;
    int xPosition = rand() % screenMax + screenMin;
    bomba->setPosition(ccp(xPosition, -spriteSize.height/2));

    // pick a random speed
    float speed = (4 - 2) * (float)rand()/(float)RAND_MAX + 2;

    CCMoveTo* actionMove = CCMoveTo::actionWithDuration(speed, ccp(xPosition, screen.height + spriteSize.height + 50));

    CCFiniteTimeAction* actionMoveDone = 
        CCCallFuncN::actionWithTarget(this, callfuncN_selector(HelloWorld::bombaMoveFinished));
    bomba->runAction(CCSequence::actions(actionMove, actionMoveDone, NULL));

    // do we have candy?
    if(rand() % 10 > 6)
    {
        int candyColor = rand() % 5;

        CCSprite* candy = getCandySprite();
        bomba->addChild(candy);
        candy->setPosition(ccp(bomba->boundingBox().size.width/2 , -50));
        candy->setTag(RISING);

        switch(candyColor)
        {
        case 0: // black
            candy->setDisplayFrame(CCSpriteFrameCache::sharedSpriteFrameCache()->spriteFrameByName("lolipop_bluen.png"));
            break;
        case 1: // orange
            candy->setDisplayFrame(CCSpriteFrameCache::sharedSpriteFrameCache()->spriteFrameByName("lolipop_green.png"));
            break;
        case 2: // pink
            candy->setDisplayFrame(CCSpriteFrameCache::sharedSpriteFrameCache()->spriteFrameByName("lolipop_purple.png"));
            break;
        case 3: // blue
            candy->setDisplayFrame(CCSpriteFrameCache::sharedSpriteFrameCache()->spriteFrameByName("lolipop_red.png"));
            break;
        case 4: // yellow
            candy->setDisplayFrame(CCSpriteFrameCache::sharedSpriteFrameCache()->spriteFrameByName("lolipop_yellow.png"));
            break;
         }
    }
}

void HelloWorld::bombaMoveFinished(CCNode* sender)
{
    CCSprite* bomba = (CCSprite*)sender;

    // Does the bomba have children?
    CCArray* children = bomba->getChildren();
    if(children != NULL && children->count() > 0)
    {
        CCSprite* child = (CCSprite*)children->objectAtIndex(0);
        child->setTag(READY);
        child->removeFromParentAndCleanup(true);
    }

    bomba->removeFromParentAndCleanup(true);
}

CCSprite* HelloWorld::getBombaSprite()
{
    for(int x = 0; x < bombaSprites.count(); ++x)
    {
        CCSprite* bomba = bombaSprites.getObjectAtIndex(x);
        CCNode* parent = bomba->getParent();
        if(parent == NULL)
        {
             return bomba;
        }
    }
    
    CCSprite* sprite = CCSprite::spriteWithSpriteFrame(CCSpriteFrameCache::sharedSpriteFrameCache()->spriteFrameByName("bomba_black.png"));

    bombaSprites.addObject(sprite);
    sprite->retain();

    return sprite;
}

CCSprite* HelloWorld::getCandySprite()
{
    for(int x = 0; x < candySprites.count(); ++x)
    {
        CCSprite* candy = candySprites.getObjectAtIndex(x);
        CCNode* parent = candy->getParent();
        if(parent == NULL)
        {
            candy->setTag(READY);
            return candy;
        }
    }
    
    CCSprite* candy = CCSprite::spriteWithSpriteFrame(CCSpriteFrameCache::sharedSpriteFrameCache()->spriteFrameByName("lolipop_green.png"));
    candySprites.addObject(candy);
    candy->setTag(READY);
    candy->retain();

    return candy;
}

/*
bool HelloWorld::ccTouchBegan(CCTouch *pTouch, CCEvent *pEvent)
{
    float pepperY = mPepper->mCurrentPepper->getPosition().y;
    float touchX = pTouch->locationInView(pTouch->view()).x;

    mPepper->mCurrentPepper->setPosition(ccp(touchX - mPepper->mCurrentPepper->boundingBox().size.width/2, pepperY));
    return true;
}
*/

#include <iostream>
#include <sstream>

void HelloWorld::ccTouchesMoved(CCSet *pTouches, CCEvent *pEvent)
{
    CCSetIterator it = pTouches->begin();
    CCTouch* touch = (CCTouch*)(*it);

    CCPoint touchLocation = touch->locationInView( touch->view() );	
    touchLocation = CCDirector::sharedDirector()->convertToGL( touchLocation );

    float pepperY = mPepper->mCurrentPepper->getPosition().y;
    float pepperX = touchLocation.x;

    if(pepperX < -mPepper->mCurrentPepper->boundingBox().size.width/2)
        pepperX = -mPepper->mCurrentPepper->boundingBox().size.width/2;
    else if(pepperX > CCDirector::sharedDirector()->getWinSize().width - mPepper->mCurrentPepper->boundingBox().size.width*0.25f)
        pepperX = CCDirector::sharedDirector()->getWinSize().width - mPepper->mCurrentPepper->boundingBox().size.width*0.25f;

    mPepper->mCurrentPepper->setPosition(ccp(pepperX - mPepper->mCurrentPepper->boundingBox().size.width*0.25f, pepperY));
  
}

void HelloWorld::ccTouchesBegan(CCSet *pTouches, CCEvent *pEvent)
{
    CCSetIterator it = pTouches->begin();
    CCTouch* touch = (CCTouch*)(*it);

    CCPoint touchLocation = touch->locationInView( touch->view() );	
    touchLocation = CCDirector::sharedDirector()->convertToGL( touchLocation );

    float pepperY = mPepper->mCurrentPepper->getPosition().y;
    float pepperX = touchLocation.x;

    if(pepperX < 0)
        pepperX = 0;
    else if(pepperX > CCDirector::sharedDirector()->getWinSize().width)
        pepperX = CCDirector::sharedDirector()->getWinSize().width;

    mPepper->mCurrentPepper->setPosition(ccp(pepperX - mPepper->mCurrentPepper->boundingBox().size.width/2, pepperY));
}


void HelloWorld::menuCloseCallback(CCObject* pSender)
{
	CCDirector::sharedDirector()->end();

#if (CC_TARGET_PLATFORM == CC_PLATFORM_IOS)
	exit(0);
#endif
}

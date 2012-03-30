#include "Pepper.h"


Pepper::Pepper(CCLayer* layer)
{
    mHappySprite = CCSprite::spriteWithSpriteFrame(CCSpriteFrameCache::sharedSpriteFrameCache()->spriteFrameByName("chili_happy.png"));
    mHappySprite->setAnchorPoint(CCPointZero);
    mHappySprite->setIsVisible(false);
    layer->addChild(mHappySprite, 10);
    mHappySprite->retain();
    
    mReloadSprite = CCSprite::spriteWithSpriteFrame(CCSpriteFrameCache::sharedSpriteFrameCache()->spriteFrameByName("chili_loading.png"));
    mReloadSprite->setAnchorPoint(CCPointZero);
    mReloadSprite->setIsVisible(false);
    layer->addChild(mReloadSprite, 10);
    mReloadSprite->retain();
    
    mShotSprite = CCSprite::spriteWithSpriteFrame(CCSpriteFrameCache::sharedSpriteFrameCache()->spriteFrameByName("chili_fire.png"));
    mShotSprite->setAnchorPoint(CCPointZero);
    mShotSprite->setIsVisible(false);
    layer->addChild(mShotSprite, 10);
    mShotSprite->retain();

    mCurrentPepper = mHappySprite;

    mPow = CCSprite::spriteWithSpriteFrame(CCSpriteFrameCache::sharedSpriteFrameCache()->spriteFrameByName("pow.png"));
    mShotSprite->addChild(mPow);
    mPow->setPosition(ccp(mShotSprite->boundingBox().size.width/2 + mPow->boundingBox().size.width/2 -8, mShotSprite->boundingBox().size.height - 10));
    mPow->retain();

    setState(HAPPY);
}


void Pepper::setState(int state)
{
    mHappySprite->setIsVisible(false);
    mReloadSprite->setIsVisible(false);
    mShotSprite->setIsVisible(false);
    
    switch(state)
    {
    case HAPPY:
        mHappySprite->setPosition(mCurrentPepper->getPosition());
        mHappySprite->setIsVisible(true);
        mCurrentPepper = mHappySprite;
        break;
    case RELOADING:
        mReloadSprite->setPosition(mCurrentPepper->getPosition());
        mReloadSprite->setIsVisible(true);
        mCurrentPepper = mReloadSprite;
        break;
    case FIRING:
        mShotSprite->setPosition(mCurrentPepper->getPosition());
        mShotSprite->setIsVisible(true);
        mCurrentPepper = mShotSprite;
        break;
    }

    this->state = state;
}
#ifndef __PEPPER_H__
#define __PEPPER_H__

#include "cocos2d.h"
USING_NS_CC;
class Pepper
{
public:
    Pepper(CCLayer* layer);
    ~Pepper(void)
    {
        mHappySprite->release();
        mReloadSprite->release();
        mShotSprite->release();
    };

    enum State
    {
        HAPPY,
        FIRING,
        RELOADING
    };

    void setState(int state);

    CCSprite* mHappySprite;
    CCSprite* mReloadSprite;
    CCSprite* mShotSprite;
    CCSprite* mCurrentPepper;

    CCSprite* mPow;
 int state;

   
};

#endif // __HELLOWORLD_SCENE_H__


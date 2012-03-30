#ifndef __HELLOWORLD_SCENE_H__
#define __HELLOWORLD_SCENE_H__

#include "cocos2d.h"
#include "Pepper.h"

USING_NS_CC;
class HelloWorld : public cocos2d::CCLayer
{
public:

	// Here's a difference. Method 'init' in cocos2d-x returns bool, instead of returning 'id' in cocos2d-iphone
	virtual bool init(); 

	// there's no 'id' in cpp, so we recommand to return the exactly class pointer
	static cocos2d::CCScene* scene();
	
	// a selector callback
	virtual void menuCloseCallback(CCObject* pSender);

    void launchTimer(ccTime delta);
    void fireTimer(ccTime delta);
    void update(ccTime delta);

	// implement the "static node()" method manually
	LAYER_NODE_FUNC(HelloWorld);

    CCMutableArray<CCSprite*> bombaSprites;
    CCMutableArray<CCSprite*> candySprites;

    CCSprite *mSpriteBackground;

    CCLabelTTF* mScoreText;
    int mScore;

    Pepper* mPepper;
    CCSprite* mSeed;
    CCSprite* getBombaSprite();
    CCSprite* getCandySprite();

    void bombaMoveFinished(CCNode* sender);

    void addScore(int points);

 //   virtual bool ccTouchBegan(CCTouch *pTouch, CCEvent *pEvent);
 //   virtual void ccTouchesMoved(CCSet *pTouches, CCEvent *pEvent);

    virtual void ccTouchesBegan(CCSet *pTouches, CCEvent *pEvent);
    virtual void ccTouchesMoved(CCSet *pTouches, CCEvent *pEvent);

};

#endif // __HELLOWORLD_SCENE_H__

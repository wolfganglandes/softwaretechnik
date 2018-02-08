--- module (NICHT AENDERN!)
module CannonBot where
--- imports (NICHT AENDERN!)
import Data.Char
import Util

--- external signatures (NICHT AENDERN!)
getMove :: String -> String
listMoves :: String -> String

--- YOUR IMPLEMENTATION STARTS HERE ---
getMove outputW =  take 5 (tail (listMoves outputW ))

listMoves input = if(last input == 'w' && not('W' `elem` input))
then "[b9-b9"
else if(last input == 'b' && not('B' `elem` input))
then "[b0-b0"
else if(last input == 'w')
then  let asdf = input
          player = last asdf
          field2 = init (init asdf)
          field111 = build field2
          containsw = testfig 9 0 'w' field111 []
          containsb = testfig 9 0 'b' field111 []
          containsW = testCity 9 0 'w' field111 []
          containsB = testCity 9 0 'b' field111 []
          containsAll = containsw ++ containsb ++ containsW ++ containsB
          isEmpty = checkisEmpty containsAll  [0..99] []
          in "[" ++ init (movesW isEmpty containsw containsb containsW containsB containsw  (goalW isEmpty containsw containsb containsW containsB [( head containsw)] [])  "") ++"]"
else if(last input == 'b')
then  let asdf = input
          player = last asdf
          field2 = init(init asdf)
          field111 = build field2
          containsw = testfig 9 0 'w' field111 []
          containsb = testfig 9 0 'b' field111 []
          containsW = testCity 9 0 'w' field111 []
          containsB = testCity 9 0 'b' field111 []
          containsAll = containsw ++ containsb ++ containsW ++ containsB
          isEmpty =  checkisEmpty containsAll [0..99] []
          containsField = [0..99] in "["++ init (movesB isEmpty containsw containsb containsW containsB containsb  (goalB isEmpty containsw containsb containsW containsB [( head containsb)][])  "") ++"]"
else "[]"



-- Alles in Array format: Ergebnis: 
--testL 9 0 'b' field111 []
--[23,25,26] bedeutet schwarze figuren auf Feldern C3,C5,C6
testfig :: Int->Int->Char->String->[Int]->[Int]
testfig _    _  _ [] out = out
testfig (-1) _  _ _  out= out
testfig _    11 _ _  out= out
testfig x y c str    out = if((head str) == '/')then testfig (x-1) (0) c   (tail str) (out)
else if(c=='w' && (head str)=='w') then testfig (x) (y+1) c  (tail str)  (out ++ [(x*10+y)])
else if(c=='b' && (head str)=='b') then testfig (x) (y+1) c  (tail str) (out ++ [(x*10+y)])
else testfig (x) (y+1) c  (tail str) ( out) 

testCity :: Int->Int->Char->String->[Int]->[Int]
testCity _    _  _ [] out = out
testCity (-1) _  _ _  out= out
testCity _    11 _ _  out= out
testCity x y c str    out = if((head str) == '/')then testCity (x-1) (0) c   (tail str) (out)
else if(c=='w' && (head str)=='W') then testCity (x) (y+1) c  (tail str)  (out ++ [(x*10+y)])
else if(c=='b' && (head str)=='B') then testCity (x) (y+1) c  (tail str) (out ++ [(x*10+y)])
else testCity (x) (y+1) c  (tail str) ( out) 


checkisEmpty :: [Int]->[Int]->[Int]->[Int]
checkisEmpty _ [] out = out
checkisEmpty containsAll a out = if(head a `elem` containsAll)
then checkisEmpty containsAll (tail a) out 
else checkisEmpty containsAll (tail a) (out ++ [(head a)])

getY :: Int -> Int
getY y = y `mod` 10

getX :: Int -> Int
getX x = (x - (x `mod` 10)) `div` 10

isNum :: Char -> Bool
isNum '2' = True
isNum '3' = True
isNum '4' = True
isNum '5' = True
isNum '6' = True
isNum '7' = True
isNum '8' = True
isNum '9' = True
isNum _ = False

build :: String ->  String
build [] = []
build x 
    | (head x == '/' && head(tail x)=='/') ="/1111111111" ++ build (drop 1 x)
    | not (isNum (head x)) = (take 1 x) ++ build (drop 1 x)
    | (head x) == '2' = "11" ++ build (drop 1 x)
    | (head x) == '3' = "111" ++ build (drop 1 x)
    | (head x) == '4' = "1111" ++ build (drop 1 x)
    | (head x) == '5' = "11111" ++ build (drop 1 x)
    | (head x) == '6' = "111111" ++ build (drop 1 x)
    | (head x) == '7' = "1111111" ++ build (drop 1 x)
    | (head x) == '8' = "11111111" ++ build (drop 1 x)
    | (head x) == '9' = "111111111" ++ build (drop 1 x)



--setField = Int -> Int -> String 
-- Blanko A9 == 9 0 daher aufruf:
-- testL 9 0 'b' field111 ""

testL :: Int->Int->Char->String->String->String
testL _    _  _ [] out = out
testL (-1) _  _ _  out= out
testL _    11 _ _  out= out
testL x y c str    out = if((head str) == '/')then testL (x-1) (0) c   (tail str) (out)
else if(c=='w' && (head str)=='w') then testL (x) (y+1) c  (tail str)  (out  ++ show x ++ show y++", ")
else if(c=='b' && (head str)=='b') then testL (x) (y+1) c  (tail str) (out  ++ show x ++ show y++", ") 
else testL (x) (y+1) c  (tail str) ( out) 



--Goalfields
-- go through array of start and output array of goals
-- goalW [23, 25, 49] [] 
goalW :: [Int]->[Int]->[Int]->[Int]->[Int]->[Int]->[Int]->[Int]
goalW _  _ _ _ _ [] out = out
--CheckBasic movemenet
goalW isEmpty containsw containsb containsW containsB arr out = if((inField ((getX (head arr)-1)) ((getY (head arr)-1))) && not (((head arr)-9) `elem` containsw) && not (((head arr)-9) `elem` out)) 
then  goalW isEmpty containsw containsb containsW containsB (arr) (out ++ [((head arr)-9) ])

else if((inField ((getX (head arr)-1)) ((getY (head arr)+1))) && not (((head arr)-11) `elem` containsw) && not (((head arr)-11) `elem` out))
then  goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)-11) ])
else if( (inField ((getX (head arr)-1)) ((getY (head arr)))) && not (((head arr)-10) `elem` containsw) && not (((head arr)-10) `elem` out))
then  goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)-10) ])

--Check Sidehits
else if( (((head arr)-1) `elem` (containsb++containsB)) && not (((head arr)-1) `elem` out) && (inField ((getX (head arr))) ((getY( head arr)-1))))
then  goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)-1) ])
else if((((head arr)+1) `elem` (containsb++containsB)) && not (((head arr)+1) `elem` out) && (inField ((getX (head arr))) ((getY (head arr)+1)))) 
then  goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)+1) ]) 

--Retreat
else if((inField ((getX (head arr)+2)) ((getY (head arr)))) && allNei (head arr) containsb && not (((head arr)+20) `elem` out) && ( (((head arr)+10) `elem` isEmpty)) && ( (((head arr)+20) `elem` isEmpty)) )
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)+20) ]) 
else if((inField ((getX (head arr)+2)) ((getY (head arr)+2))) && allNei (head arr) containsb && not (((head arr)+22) `elem` out) && ( (((head arr)+11) `elem` isEmpty)) && ( (((head arr)+22) `elem` isEmpty)) )
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)+22) ]) 
else if((inField ((getX (head arr)+2)) ((getY( head arr)-2))) && allNei (head arr) containsb && not (((head arr)+18) `elem` out) && ( (((head arr)+9) `elem` isEmpty)) && ( (((head arr)+18) `elem` isEmpty)) )
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)+18) ]) 

--CannonMove
else if((inField ((getX (head arr)-3)) ((getY(  head arr)))) && not (((head arr)-30) `elem` out)&& ((head arr)-30) `elem` isEmpty && ((head arr)-20) `elem` containsw && ((head arr)-10) `elem` containsw)
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)-30) ])
else if((inField ((getX (head arr)+3)) ((getY(  head arr)))) && not (((head arr)+30) `elem` out)&& ((head arr)+30) `elem` isEmpty && ((head arr)+20) `elem` containsw && ((head arr)+10) `elem` containsw)
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)+30) ]) 
else if((inField ((getX (head arr)-3)) ((getY(  head arr)-3))) && not (((head arr)-33) `elem` out)&& ((head arr)-33) `elem` isEmpty && ((head arr)-22) `elem` containsw && ((head arr)-11) `elem` containsw)
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)-33) ]) 
else if((inField ((getX (head arr)+3)) ((getY(  head arr)+3))) && not (((head arr)+33) `elem` out)&& ((head arr)+33) `elem` isEmpty && ((head arr)+22) `elem` containsw && ((head arr)+11) `elem` containsw)
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)+33) ])
else if((inField ((getX (head arr))) ((getY(  head arr)-3))) && not (((head arr)-3) `elem` out)&& ((head arr)-3) `elem` isEmpty && ((head arr)-2) `elem` containsw && ((head arr)-1) `elem` containsw)
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)-3) ]) 
else if((inField ((getX (head arr))) ((getY(  head arr)+3))) && not (((head arr)+3) `elem` out)&& ((head arr)+3) `elem` isEmpty && ((head arr)+2) `elem` containsw && ((head arr)+1) `elem` containsw)
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)+3)]) 
else if((inField ((getX (head arr)-3)) ((getY(  head arr)+3))) && not (((head arr)-27) `elem` out)&& ((head arr)-27) `elem` isEmpty && ((head arr)-18) `elem` containsw && ((head arr)-9) `elem` containsw)
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)-27) ]) 
else if((inField ((getX (head arr)+3)) ((getY(  head arr)-3))) && not (((head arr)+27) `elem` out)&& ((head arr)+27) `elem` isEmpty && ((head arr)+18) `elem` containsw && ((head arr)+9) `elem` containsw)
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)+27) ]) 

--CannonShotShort
else if((inField ((getX (head arr)-4)) ((getY(  head arr)))) && not (((head arr)-40) `elem` out) &&  ((head arr)-40) `elem` (containsb++containsB) && ((head arr)-30) `elem` isEmpty && ((head arr)-20) `elem` containsw && ((head arr)-10) `elem` containsw)
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)-30) ])
else if((inField ((getX (head arr)+4)) ((getY(  head arr)))) && not (((head arr)+40) `elem` out)&&   ((head arr)+40) `elem` (containsb++containsB) &&((head arr)+30) `elem` isEmpty && ((head arr)+20) `elem` containsw && ((head arr)+10) `elem` containsw)
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)+40) ]) 
else if((inField ((getX (head arr)-4)) ((getY(  head arr)-4))) && not (((head arr)-44) `elem` out)&&   ((head arr)-44) `elem` (containsb++containsB) &&((head arr)-33) `elem` isEmpty && ((head arr)-22) `elem` containsw && ((head arr)-11) `elem` containsw)
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)-44) ]) 
else if((inField ((getX (head arr)+4)) ((getY(  head arr)+4))) &&not (((head arr)+44) `elem` out)&&   ((head arr)-44) `elem` (containsb++containsB) &&((head arr)+33) `elem` isEmpty && ((head arr)+22) `elem` containsw && ((head arr)+11) `elem` containsw)
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)+44) ])
else if((inField ((getX (head arr))) ((getY(  head arr)-4))) && not (((head arr)-4) `elem` out)&&   ((head arr)-4) `elem` (containsb++containsB) &&((head arr)-3) `elem` isEmpty && ((head arr)-2) `elem` containsw && ((head arr)-1) `elem` containsw)
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)-4) ]) 
else if((inField ((getX (head arr))) ((getY(  head arr)+4))) &&not (((head arr)+4) `elem` out)&&   ((head arr)+4) `elem` (containsb++containsB) &&((head arr)+3) `elem` isEmpty && ((head arr)+2) `elem` containsw && ((head arr)+1) `elem` containsw)
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)+4)]) 
else if((inField ((getX (head arr)-4)) ((getY(  head arr)+4))) && not (((head arr)-36) `elem` out)&&  ( (head arr)-36) `elem` (containsb++containsB) && ((head arr)-27) `elem` isEmpty && ((head arr)-18) `elem` containsw && ((head arr)-9) `elem` containsw)
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)-36) ]) 
else if((inField ((getX (head arr)+4)) ((getY(  head arr)-4))) && not (((head arr)+36) `elem` out)&&   ((head arr)+36) `elem` (containsb++containsB) && ((head arr)+27) `elem` isEmpty && ((head arr)+18) `elem` containsw && ((head arr)+9) `elem` containsw)
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)+36) ]) 


--CannonShotLong
else if((inField ((getX (head arr)-5)) ((getY(  head arr)))) &&not (((head arr)-50) `elem` out) &&  ((head arr)-50) `elem` (containsb++containsB) && ((head arr)-30) `elem` isEmpty && ((head arr)-20) `elem` containsw && ((head arr)-10) `elem` containsw)
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)-50) ])
else if((inField ((getX (head arr)+5)) ((getY(  head arr)))) && not (((head arr)+50) `elem` out)&&   ((head arr)+50) `elem` (containsb++containsB) &&((head arr)+30) `elem` isEmpty && ((head arr)+20) `elem` containsw && ((head arr)+10) `elem` containsw)
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)+50) ]) 
else if((inField ((getX (head arr)-5)) ((getY(  head arr)-5))) && not (((head arr)-55) `elem` out)&&   ((head arr)-55) `elem` (containsb++containsB) &&((head arr)-33) `elem` isEmpty && ((head arr)-22) `elem` containsw && ((head arr)-11) `elem` containsw)
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)-55) ]) 
else if((inField ((getX (head arr)+5)) ((getY(  head arr)+5))) && not (((head arr)+55) `elem` out)&&   ((head arr)-55) `elem` (containsb++containsB) &&((head arr)+33) `elem` isEmpty && ((head arr)+22) `elem` containsw && ((head arr)+11) `elem` containsw)
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)+55) ])
else if((inField ((getX (head arr))) ((getY(  head arr)-5))) && not (((head arr)-5) `elem` out)&&   ((head arr)-5) `elem` (containsb++containsB) &&((head arr)-3) `elem` isEmpty && ((head arr)-2) `elem` containsw && ((head arr)-1) `elem` containsw)
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)-5) ]) 
else if((inField ((getX (head arr))) ((getY(  head arr)+5))) && not (((head arr)+5) `elem` out)&&   ((head arr)+5) `elem` (containsb++containsB) &&((head arr)+3) `elem` isEmpty && ((head arr)+2) `elem` containsw && ((head arr)+1) `elem` containsw)
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)+5)]) 
else if((inField ((getX (head arr)-5)) ((getY(  head arr)+5))) && not (((head arr)-45) `elem` out)&&  ( (head arr)-45) `elem` (containsb++containsB) && ((head arr)-27) `elem` isEmpty && ((head arr)-18) `elem` containsw && ((head arr)-9) `elem` containsw)
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)-45) ]) 
else if((inField ((getX (head arr)+5)) ((getY(  head arr)-5))) && not (((head arr)+45) `elem` out)&&   ((head arr)+45) `elem` (containsb++containsB) && ((head arr)+27) `elem` isEmpty && ((head arr)+18) `elem` containsw && ((head arr)+9) `elem` containsw)
then goalW isEmpty containsw containsb containsW containsB ( arr) (out ++ [((head arr)+45) ]) 

--Check Next Move
else goalW isEmpty containsw containsb containsW containsB (tail arr) (out)

-- BLAAAAAAACK///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
--Goalfields
-- go through array of start and output array of goals
-- goalW [23, 25, 49] [] 
goalB :: [Int]->[Int]->[Int]->[Int]->[Int]->[Int]->[Int]->[Int]
goalB _ _ _ _ _ [] out = out
--CheckBasic movemenet
goalB isEmpty containsw containsb containsW containsB arr out = if((inField ((getX (head arr))+1) ((getY(  head arr)-1)))  &&  not (((head arr)+9) `elem` containsb) && not (((head arr)+9) `elem` out) )
then  goalB isEmpty containsw containsb containsW containsB  (arr) (out ++ [((head arr)+9) ])
else if((inField ((getX (head arr)+1)) ((getY(  head arr)+1))) && not (((head arr)+11) `elem` containsb) && not (((head arr)+11) `elem` out) )
then  goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)+11) ])
else if((inField ((getX (head arr)+1)) ((getY(  head arr)))) && not (((head arr)+10) `elem` containsb) && not (((head arr)+10) `elem` out))
then  goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)+10) ])

--Check Sidehits
else if((inField ((getX (head arr))) ((getY(  head arr)+1)))  && (((head arr)+1) `elem` (containsw++containsW)) && not (((head arr)+1) `elem` out) )
then  goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)+1) ])
else if((inField ((getX (head arr))) ((getY(  head arr)-1)))  && (((head arr)-1) `elem` (containsw++containsW)) && not (((head arr)-1) `elem` out) ) 
then  goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)-1) ]) 

--Retreat
else if((inField ((getX (head arr)-2)) ((getY(  head arr))))  && allNei (head arr) containsw && not (((head arr)-20) `elem` out) && ( (((head arr)-10) `elem` isEmpty)) && ( (((head arr)-20) `elem` isEmpty)) )
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)-20) ]) 
else if((inField ((getX (head arr)-2)) ((getY(  head arr)+2))) && allNei (head arr) containsw && not (((head arr)-22) `elem` out) && ( (((head arr)-11) `elem` isEmpty)) && ( (((head arr)-22) `elem` isEmpty)) )
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)-22) ]) 
else if((inField ((getX (head arr)-2)) ((getY(  head arr)-2)))  && allNei (head arr) containsw && not (((head arr)-18) `elem` out) && ( (((head arr)-9) `elem` isEmpty)) && ( (((head arr)-18) `elem` isEmpty)) )
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)-18) ]) 

--CannonMove
else if((inField ((getX (head arr)-3)) ((getY(  head arr))))  && not (((head arr)-30) `elem` out)&& ((head arr)-30) `elem` isEmpty && ((head arr)-20) `elem` containsb && ((head arr)-10) `elem` containsb)
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)-30) ])
else if((inField ((getX (head arr)+3)) ((getY(  head arr))))  && not (((head arr)+30) `elem` out)&& ((head arr)+30) `elem` isEmpty && ((head arr)+20) `elem` containsb && ((head arr)+10) `elem` containsb)
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)+30) ]) 
else if((inField ((getX (head arr)-3)) ((getY(  head arr)-3)))  && not (((head arr)-33) `elem` out)&& ((head arr)-33) `elem` isEmpty && ((head arr)-22) `elem` containsb && ((head arr)-11) `elem` containsb)
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)-33) ]) 
else if((inField ((getX (head arr)+3)) ((getY(  head arr)+3)))  && not (((head arr)+33) `elem` out)&& ((head arr)+33) `elem` isEmpty && ((head arr)+22) `elem` containsb && ((head arr)+11) `elem` containsb)
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)+33) ])
else if((inField ((getX (head arr))) ((getY(  head arr)-3)))  && not (((head arr)-3) `elem` out)&& ((head arr)-3) `elem` isEmpty && ((head arr)-2) `elem` containsb && ((head arr)-1) `elem` containsb)
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)-3) ]) 
else if((inField ((getX (head arr))) ((getY(  head arr)+3)))  && not (((head arr)+3) `elem` out)&& ((head arr)+3) `elem` isEmpty && ((head arr)+2) `elem` containsb && ((head arr)+1) `elem` containsb)
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)+3)]) 
else if((inField ((getX (head arr)-3)) ((getY(  head arr)+3)))  && not (((head arr)-27) `elem` out)&& ((head arr)-27) `elem` isEmpty && ((head arr)-18) `elem` containsb && ((head arr)-9) `elem` containsb)
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)-27) ]) 
else if((inField ((getX (head arr)+3)) ((getY(  head arr)-3)))  && not (((head arr)+27) `elem` out)&& ((head arr)+27) `elem` isEmpty && ((head arr)+18) `elem` containsb && ((head arr)+9) `elem` containsb)
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)+27) ]) 

--CannonShotShort
else if((inField ((getX (head arr)-4)) ((getY(  head arr))))  && not (((head arr)-40) `elem` out) &&  ((head arr)-40) `elem` (containsw++containsW) && ((head arr)-30) `elem` isEmpty && ((head arr)-20) `elem` containsb && ((head arr)-10) `elem` containsb)
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)-30) ])
else if((inField ((getX (head arr)+4)) ((getY(  head arr))))  && not (((head arr)+40) `elem` out)&&   ((head arr)+40) `elem` (containsw++containsW) &&((head arr)+30) `elem` isEmpty && ((head arr)+20) `elem` containsb && ((head arr)+10) `elem` containsb)
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)+40) ]) 
else if((inField ((getX (head arr)-4)) ((getY(  head arr)-4)))  && not (((head arr)-44) `elem` out)&&   ((head arr)-44) `elem` (containsw++containsW) &&((head arr)-33) `elem` isEmpty && ((head arr)-22) `elem` containsb && ((head arr)-11) `elem` containsb)
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)-44) ]) 
else if((inField ((getX (head arr)+4)) ((getY(  head arr)+4))) && not (((head arr)+44) `elem` out)&&   ((head arr)-44) `elem` (containsw++containsW) &&((head arr)+33) `elem` isEmpty && ((head arr)+22) `elem` containsb && ((head arr)+11) `elem` containsb)
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)+44) ])
else if((inField ((getX (head arr))) ((getY(  head arr)+4))) && not (((head arr)-4) `elem` out)&&   ((head arr)-4) `elem` (containsw++containsW) &&((head arr)-3) `elem` isEmpty && ((head arr)-2) `elem` containsb && ((head arr)-1) `elem` containsb)
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)-4) ]) 
else if((inField ((getX (head arr))) ((getY(  head arr)-4)))  && not (((head arr)+4) `elem` out)&&   ((head arr)+4) `elem` (containsw++containsW) &&((head arr)+3) `elem` isEmpty && ((head arr)+2) `elem` containsb && ((head arr)+1) `elem` containsb)
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)+4)]) 
else if((inField ((getX (head arr)-4)) ((getY(  head arr) +4)))  && not (((head arr)-36) `elem` out)&&  ( (head arr)-36) `elem` (containsw++containsW) && ((head arr)-27) `elem` isEmpty && ((head arr)-18) `elem` containsb && ((head arr)-9) `elem` containsb)
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr-36)) ]) 
else if((inField ((getX (head arr)+4)) ((getY(  head arr)-4)))  && not (((head arr)+36) `elem` out)&&   ((head arr)+36) `elem` (containsw++containsW) && ((head arr)+27) `elem` isEmpty && ((head arr)+18) `elem` containsb && ((head arr)+9) `elem` containsb)
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)+36) ]) 


--CannonShotLong
else if((inField ((getX (head arr)-5)) ((getY(  head arr))))  && not (((head arr)-50) `elem` out) &&  ((head arr)-50) `elem` (containsw++containsW) && ((head arr)-30) `elem` isEmpty && ((head arr)-20) `elem` containsb && ((head arr)-10) `elem` containsb)
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)-50) ])
else if((inField ((getX (head arr)+5)) ((getY(  head arr)))) && not (((head arr)+50) `elem` out)&&   ((head arr)+50) `elem` (containsw++containsW) &&((head arr)+30) `elem` isEmpty && ((head arr)+20) `elem` containsb && ((head arr)+10) `elem` containsb)
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)+50) ]) 
else if((inField ((getX (head arr)-5)) ((getY(  head arr)-5)))  && not (((head arr)-55) `elem` out)&&   ((head arr)-55) `elem` (containsw++containsW) &&((head arr)-33) `elem` isEmpty && ((head arr)-22) `elem` containsb && ((head arr)-11) `elem` containsb)
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)-55) ]) 
else if((inField ((getX (head arr)+5)) ((getY(  head arr)+5)))  && not (((head arr)+55) `elem` out)&&   ((head arr)-55) `elem` (containsw++containsW) &&((head arr)+33) `elem` isEmpty && ((head arr)+22) `elem` containsb && ((head arr)+11) `elem` containsb)
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)+55) ])
else if((inField ((getX (head arr))) ((getY(  head arr)-5))) && not (((head arr)-5) `elem` out)&&   ((head arr)-5) `elem` (containsw++containsW) &&((head arr)-3) `elem` isEmpty && ((head arr)-2) `elem` containsb && ((head arr)-1) `elem` containsb)
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)-5) ]) 
else if((inField ((getX (head arr))) ((getY(  head arr)+5)))  && not (((head arr)+5) `elem` out)&&   ((head arr)+5) `elem` (containsw++containsW) &&((head arr)+3) `elem` isEmpty && ((head arr)+2) `elem` containsb && ((head arr)+1) `elem` containsb)
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)+5)]) 
else if((inField ((getX (head arr)-5)) ((getY(  head arr)+5)))  && not (((head arr)-45) `elem` out)&&  ( (head arr)-45) `elem` (containsw++containsW) && ((head arr)-27) `elem` isEmpty && ((head arr)-18) `elem` containsb && ((head arr)-9) `elem` containsb)
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)-45) ]) 
else if((inField ((getX (head arr)+5)) ((getY(  head arr)-5)))  && not (((head arr)+45) `elem` out)&&   ((head arr)+45) `elem` (containsw++containsW) && ((head arr)+27) `elem` isEmpty && ((head arr)+18) `elem` containsb && ((head arr)+9) `elem` containsb)
then goalB isEmpty containsw containsb containsW containsB  ( arr) (out ++ [((head arr)+45) ]) 

--Check Next Move
else goalB isEmpty containsw containsb containsW containsB  (tail arr) (out)



allNei :: Int-> [Int]-> Bool
allNei x arr = if((inField (getX(x)) (getY(x)-1)&&((x-1) `elem` arr ))|| (inField (getX(x)) (getY(x)+1)&&((x+1) `elem` arr ))|| (inField (getX(x)-1) (getY(x)-1)&&((x-11) `elem` arr ))|| (inField (getX(x)-1) (getY(x))&&((x-10) `elem` arr ))|| (inField (getX(x)-1) (getY(x)+1)&&((x-9) `elem` arr ))|| (inField (getX(x)+1) (getY(x)-1)&&((x+9) `elem` arr ))|| (inField (getX(x)+1) (getY(x))&&((x+10) `elem` arr ))|| (inField (getX(x)+1) (getY(x)+1)&&((x+11) `elem` arr )))
then True
else False

--formats movesW together. 
-- all figures -> move list of each figure ->output
movesW :: [Int]->[Int]->[Int]->[Int]->[Int]->[Int]->[Int]->String->String
movesW _ _ _ _ _ [] _ out = out
movesW isEmpty containsw containsb containsW containsB fig [] out = movesW isEmpty containsw containsb containsW containsB (tail fig) (goalW isEmpty containsw containsb containsW containsB [(head(tail fig))] []) out
movesW isEmpty containsw containsb containsW containsB fig mov out = out  ++ convertA (head fig) ++ "-" ++ convertA(head mov) ++ "," ++ movesW isEmpty containsw containsb containsW containsB fig (tail mov) out

--formats movesB together. 
-- all figures -> move list of each figure ->output
movesB :: [Int]->[Int]->[Int]->[Int]->[Int]->[Int]->[Int]->String->String
movesB _ _ _ _ _ [] _ out = out
movesB isEmpty containsw containsb containsW containsB fig [] out = movesB isEmpty containsw containsb containsW containsB (tail fig) (goalB isEmpty containsw containsb containsW containsB [(head(tail fig))] []) out
movesB isEmpty containsw containsb containsW containsB fig mov out = out  ++ convertA (head fig) ++ "-" ++ convertA(head mov) ++ "," ++ movesB isEmpty containsw containsb containsW containsB fig (tail mov) out

inField :: Int->Int->Bool
inField x y = if(x<=9&&x>=0&&y<=9&&y>=0)then True else False

convertA :: Int->String
convertA x 
 | ( x `mod` 10) == 0 =  "a" ++ show((x - (x `mod` 10)) `div` 10)
 | ( x `mod` 10) == 1 =  "b" ++ show((x - (x `mod` 10)) `div` 10)
 | ( x `mod` 10) == 2 =  "c" ++ show((x - (x `mod` 10)) `div` 10)
 | ( x `mod` 10) == 3 =  "d" ++ show((x - (x `mod` 10)) `div` 10)
 | ( x `mod` 10) == 4 =  "e" ++ show((x - (x `mod` 10)) `div` 10)
 | ( x `mod` 10) == 5 =  "f" ++ show((x - (x `mod` 10)) `div` 10)
 | ( x `mod` 10) == 6 =  "g" ++ show((x - (x `mod` 10)) `div` 10)
 | ( x `mod` 10) == 7 =  "h" ++ show((x - (x `mod` 10)) `div` 10)
 | ( x `mod` 10) == 8 =  "i" ++ show((x - (x `mod` 10)) `div` 10)
 | ( x `mod` 10) == 9 =  "j" ++ show((x - (x `mod` 10)) `div` 10)


## Calculate square diffs between given sample and its neighbours
function [DSUBJ, DCOND, sigmaS, sigmaC] = calcSampleDiffs(imgDir, subj, cond, subjN, condN, subjTol, condTol) 
	DSUBJ = zeros(subjN, 1);
	DCOND = zeros(condN, 1);
	IMG = (imread(sprintf("%s/bysubj/%02d/%04d.jpg", imgDir, subj, cond))/255.0)(:);
	for subjI=1:subjN
	  imgK = imread(sprintf("%s/bysubj/%02d/%04d.jpg", imgDir, subjI, cond))/255.0;
	  diff = IMG-imgK(:);
	  DSUBJ(subjI) = sum(diff.*diff);
	endfor
	for condI=1:condN
	  imgK = imread(sprintf("%s/bysubj/%02d/%04d.jpg", imgDir, subj, condI))/255.0;
	  diff = IMG-imgK(:);
	  DCOND(condI) = dot(diff,diff);
	endfor

	DCOND = sqrt(DCOND);
	DSUBJ = sqrt(DSUBJ);
	sigmaC = getsigma(subjTol, DCOND);
	sigmaS = getsigma(condTol, DSUBJ);
endfunction

function [sigma] = getsigma(part, D) 
  ds = sort(D);
  i = round(part*length(ds));
  if i==0
    sigma = 0;
  else
    sigma = ds(i);
  endif
endfunction

## analyze bootstap images

function bootstrap(dbDir, matDir, outputDir, m, w, h, nb)
  bTotal = NaN;
  nTotal = NaN;
  d = w*h;
  B = zeros(nb, d, m);
  for k=1:m
    subjectName = sprintf("%02d", k);
    subjectDir = sprintf("%s/%s/%s", dbDir, subjectName, matDir);
    #fprintf("Processing %s...\n", subjectDir);
    normMat = readNorm(subjectDir);
    bMat = calcBasis(normMat);
    normV = normMat(:,:);
    bV = bMat(:,:);
    if isnan(bTotal)
      bTotal = bV;
    else
      bTotal = [bTotal bV];
    endif
    if isnan(nTotal)
      nTotal = normV;
    else
      nTotal = [nTotal normV];
    endif
    B(:, :, k) = bV;
  endfor
  bMean = zeros(nb, d);
  bMean = mean(B, 3);
  bCov = zeros(nb, nb, d);
  for x=1:d
    observations = zeros(nb, m);
    observations(:,:) = B(:,x,:);
    bCov(:,:,x) = cov(observations'); # covariance of B for x'th pixel
  endfor
  lights = generateLight(2);
  condCnt = rows(lights);
  alpha = cell(condCnt, 1);
  for i=1:condCnt    # IMG = B'*aplha+E
    IMG = applyLightCond(nTotal, lights{i, 1}, lights{i, 2});
    [ALPHA, SIGMA, E] = ols(IMG, bTotal');
    EE = zeros(d, m);
    EE(:) = E;
    meanE = mean(EE, 2);
    varE = var(EE, 0, 2);
    subject = 1;
    for j=1:d:length(IMG)
      II = zeros(w, h);
      II(:) = IMG(j:j+d-1);
      fileName = sprintf("%s/%02d.%04d.jpg", outputDir, subject++, i);
      imwrite(II, fileName, "jpeg", "Quality", 100);
    endfor
    alpha(i) = struct("alpha", ALPHA, "meanE", meanE, "varE", varE, "condition", i);
  endfor
  save(sprintf("%s/bootstrap.data", outputDir), "bMean", "bCov", "alpha");
end

function showVec(V, w, h)
  VV = zeros(h, w);
  VV(:) = V(:);
  imshow(VV);
end